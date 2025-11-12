package com.example.dzcoffee

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AdminMenuActivity : AppCompatActivity() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private lateinit var spFilterCategory: Spinner
    private lateinit var spCategory: Spinner
    private lateinit var edtName: EditText
    private lateinit var edtPrice: EditText
    private lateinit var edtIngredients: EditText
    private lateinit var edtImageKey: EditText
    private lateinit var chkIsCoffee: CheckBox
    private lateinit var chkAllowOptions: CheckBox
    private lateinit var btnSave: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var btnPickImage: MaterialButton
    private lateinit var imgPreview: ImageView
    private lateinit var layoutMenuList: LinearLayout

    private var editingId: String? = null
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String? = null

    private val categoryOptions = listOf("Coffee", "Snack", "Dessert", "Other")
    private val filterOptions = listOf("All") + categoryOptions

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imgPreview.setImageURI(uri)
        }
    }

    data class MenuItem(
        val id: String,
        val name: String,
        val category: String,
        val basePrice: Double,
        val ingredients: String,
        val imageKey: String?,
        val imageUrl: String?,
        val isCoffee: Boolean,
        val allowOptions: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        spFilterCategory = findViewById(R.id.spFilterCategory)
        spCategory = findViewById(R.id.spCategory)
        edtName = findViewById(R.id.edtName)
        edtPrice = findViewById(R.id.edtPrice)
        edtIngredients = findViewById(R.id.edtIngredients)
        edtImageKey = findViewById(R.id.edtImageKey)
        chkIsCoffee = findViewById(R.id.chkIsCoffee)
        chkAllowOptions = findViewById(R.id.chkAllowOptions)
        btnSave = findViewById(R.id.btnSaveItem)
        btnClear = findViewById(R.id.btnClearForm)
        btnPickImage = findViewById(R.id.btnPickImage)
        imgPreview = findViewById(R.id.imgPreview)
        layoutMenuList = findViewById(R.id.layoutMenuList)

        setupSpinners()

        btnSave.setOnClickListener { saveItem() }
        btnClear.setOnClickListener { clearForm() }
        btnPickImage.setOnClickListener { openImagePicker() }

        spFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = filterOptions[position]
                loadMenuForCategory(if (selected == "All") null else selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                loadMenuForCategory(null)
            }
        }

        clearForm()
        loadMenuForCategory(null)
    }

    private fun setupSpinners() {
        val filterAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            filterOptions
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilterCategory.adapter = filterAdapter

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryOptions
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = categoryAdapter
        spCategory.setSelection(0)
    }

    private fun openImagePicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun saveItem() {
        val name = edtName.text.toString().trim()
        val priceText = edtPrice.text.toString().trim()
        val ingredients = edtIngredients.text.toString().trim()
        val imageKey = edtImageKey.text.toString().trim().ifBlank { null }
        val isCoffee = chkIsCoffee.isChecked
        val allowOptions = chkAllowOptions.isChecked
        val category = spCategory.selectedItem?.toString() ?: "Other"

        if (name.isEmpty()) {
            edtName.error = "Name required"
            return
        }
        val price = priceText.toDoubleOrNull()
        if (price == null || price < 0.0) {
            edtPrice.error = "Enter valid price"
            return
        }

        val id = editingId ?: slug("$name-$category")

        val data = hashMapOf<String, Any?>(
            "name" to name,
            "category" to category,
            "basePrice" to price,
            "ingredients" to ingredients,
            "isCoffee" to isCoffee,
            "allowOptions" to allowOptions
        )
        if (imageKey != null) {
            data["imageKey"] = imageKey
        }

        if (currentImageUrl != null && selectedImageUri == null) {
            data["imageUrl"] = currentImageUrl
        }

        val uri = selectedImageUri
        if (uri != null) {
            val imageRef = storage.reference.child("menu_images/$id.jpg")

            imageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: RuntimeException("Upload failed")
                    }
                    imageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    currentImageUrl = downloadUri.toString()
                    data["imageUrl"] = currentImageUrl
                    selectedImageUri = null
                    saveItemDocument(id, data)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            saveItemDocument(id, data)
        }
    }

    private fun saveItemDocument(id: String, data: Map<String, Any?>) {
        db.collection("menu").document(id)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    if (editingId == null) "Item added" else "Item updated",
                    Toast.LENGTH_SHORT
                ).show()
                clearForm()
                val filterCat = spFilterCategory.selectedItem?.toString()
                loadMenuForCategory(if (filterCat == null || filterCat == "All") null else filterCat)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearForm() {
        editingId = null
        selectedImageUri = null
        currentImageUrl = null

        edtName.setText("")
        edtPrice.setText("")
        edtIngredients.setText("")
        edtImageKey.setText("")
        chkIsCoffee.isChecked = false
        chkAllowOptions.isChecked = false
        spCategory.setSelection(0)
        btnSave.text = "Add item"

        imgPreview.setImageResource(R.drawable.ic_dz_coffee_cup)
    }

    private fun loadMenuForCategory(category: String?) {
        layoutMenuList.removeAllViews()

        val query = if (category == null) {
            db.collection("menu").orderBy("name")
        } else {
            db.collection("menu").whereEqualTo("category", category)
        }

        query.get()
            .addOnSuccessListener { snap ->
                val inflater = LayoutInflater.from(this)

                if (snap.isEmpty) {
                    val tv = TextView(this)
                    tv.text = "No items for this category"
                    layoutMenuList.addView(tv)
                    return@addOnSuccessListener
                }

                val items = snap.documents.map { d ->
                    MenuItem(
                        id = d.id,
                        name = d.getString("name") ?: d.id,
                        category = d.getString("category") ?: "Other",
                        basePrice = d.getDouble("basePrice") ?: 0.0,
                        ingredients = d.getString("ingredients") ?: "",
                        imageKey = d.getString("imageKey"),
                        imageUrl = d.getString("imageUrl"),
                        isCoffee = d.getBoolean("isCoffee") ?: false,
                        allowOptions = d.getBoolean("allowOptions") ?: false
                    )
                }.sortedWith(compareBy<MenuItem> { it.category }.thenBy { it.name })

                for (item in items) {
                    val row = inflater.inflate(
                        R.layout.row_admin_menu_item,
                        layoutMenuList,
                        false
                    )

                    row.findViewById<TextView>(R.id.tvItemName).text = item.name
                    row.findViewById<TextView>(R.id.tvItemCategory).text = item.category
                    row.findViewById<TextView>(R.id.tvItemPrice).text =
                        "£${String.format("%.2f", item.basePrice)}"
                    row.findViewById<TextView>(R.id.tvItemInfo).text = item.ingredients

                    val badges = mutableListOf<String>()
                    if (item.isCoffee) badges.add("coffee")
                    if (item.allowOptions) badges.add("options")
                    row.findViewById<TextView>(R.id.tvItemBadges).text =
                        badges.joinToString(" • ")

                    row.findViewById<MaterialButton>(R.id.btnEditItem).setOnClickListener {
                        startEditing(item)
                    }
                    row.findViewById<MaterialButton>(R.id.btnDeleteItem).setOnClickListener {
                        confirmDelete(item)
                    }

                    layoutMenuList.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Load failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun startEditing(item: MenuItem) {
        editingId = item.id
        edtName.setText(item.name)
        edtPrice.setText(item.basePrice.toString())
        edtIngredients.setText(item.ingredients)
        edtImageKey.setText(item.imageKey ?: "")
        chkIsCoffee.isChecked = item.isCoffee
        chkAllowOptions.isChecked = item.allowOptions

        val index = categoryOptions.indexOf(item.category)
        if (index >= 0) {
            spCategory.setSelection(index)
        }

        currentImageUrl = item.imageUrl
        selectedImageUri = null
        btnSave.text = "Update item"

        imgPreview.setImageResource(R.drawable.ic_dz_coffee_cup)
    }

    private fun confirmDelete(item: MenuItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete item")
            .setMessage("Delete \"${item.name}\" from ${item.category}?")
            .setPositiveButton("Delete") { _, _ -> deleteItem(item.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(id: String) {
        db.collection("menu").document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
                val filterCat = spFilterCategory.selectedItem?.toString()
                loadMenuForCategory(if (filterCat == null || filterCat == "All") null else filterCat)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun slug(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }
}
