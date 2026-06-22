# ☕ DZCoffee Mobile App

## 📱 Overview
DZCoffee is a modern mobile café ordering application developed in Android Studio using Kotlin.  
The app allows customers to browse products, customise drinks, place orders, and interact with a clean and intuitive interface.

The system also includes Admin and Super Admin panels for full management of the application.

---

## 🎯 Project Purpose
The purpose of this project is to demonstrate:
- Mobile application development using Android (Kotlin)
- Clean UI/UX design
- Role-based system (Customer / Admin / Super Admin)
- Order and menu management
- Use of Firebase Firestore database
- MVVM architecture principles

---

## 🚀 Features

### 👤 Customer
- Browse menu (coffee categories)
- View product details with images
- Customise drinks:
  - Size (Small / Medium / Large)
  - Milk type
  - Sugar level
- Add items to cart
- View cart and total price
- Select payment method:
  - Pay in café
  - Card on pickup
- Place orders
- View order history
- Leave feedback (rating + comment)

---

### 🛠 Admin Panel
- Manage menu:
  - Add new items
  - Edit existing items
  - Delete items
- Manage orders:
  - View orders
  - Change order status (Pending → Completed → Cancelled)
- View customer feedback

---

### 👑 Super Admin
- Register new admins
- Manage admin accounts
- Remove admins
- Full control over system

---

## 🔐 Test Accounts

You can use the following accounts to test the application:

### 👤 Customer
- Login: dami  
- Password: dami123  

### 🛠 Admin
- Login: dzadmin  
- Password: Admin123  

### 👑 Super Admin
- Available in the system (credentials hidden for security reasons)

---

## ☁️ Firebase Database

The application uses Firebase Firestore.

### Collections:
- users  
- admin  
- superadmins  
- orders  
- feedback  
- usernames  

### Example Fields

User:
- firstName  
- lastName  
- email  
- login  
- role  
- uID  
- createdAt  

Order:
- items  
- totalPrice  
- status  
- userEmail  
- date  

Feedback:
- rating  
- comment  
- userEmail  
- date  

---

## 🧱 Technologies Used

- Kotlin  
- Android Studio  
- MVVM Architecture  
- Firebase Firestore  
- Firebase Authentication  
- XML (Material Design UI)  
- Git & GitHub  

---

## 📸 Screenshots

<img width="570" height="1180" alt="Screenshot 2026-04-22 172006" src="https://github.com/user-attachments/assets/6c01237e-5e4e-4300-bddc-980660553d7c" />
<img width="535" height="1180" alt="Screenshot 2026-04-22 171945" src="https://github.com/user-attachments/assets/9ade13d7-8182-4fc3-96df-f7b755567c84" />
<img width="589" height="1180" alt="Screenshot 2026-04-22 171927" src="https://github.com/user-attachments/assets/b0748e4d-443c-4dbe-8c8a-1061c9e0e755" />
<img width="602" height="1197" alt="Screenshot 2026-04-22 171829" src="https://github.com/user-attachments/assets/77d062a5-c8a4-4434-ad5e-5a6681648a5b" />
<img width="577" height="1197" alt="Screenshot 2026-04-22 171716" src="https://github.com/user-attachments/assets/0b5b7d68-2037-484a-bc03-21a013bec3b9" />
<img width="578" height="1189" alt="Screenshot 2026-04-22 171505" src="https://github.com/user-attachments/assets/44d98342-fdf1-4800-ac2f-4129427e9091" />
<img width="625" height="1231" alt="Screenshot 2026-04-22 171442" src="https://github.com/user-attachments/assets/a30b51c9-b59a-4e4d-8d6f-b09160e8a59b" />
<img width="601" height="1187" alt="Screenshot 2026-04-22 171331" src="https://github.com/user-attachments/assets/4534798c-9b52-4f65-8222-06ffd491d6f7" />
<img width="570" height="1180" alt="Screenshot 2026-04-22 171313" src="https://github.com/user-attachments/assets/cfa52132-2c0d-427b-90f3-5387406d5435" />
<img width="600" height="1195" alt="Screenshot 2026-04-22 171254" src="https://github.com/user-attachments/assets/daf74a0a-0af4-47d5-aa35-f1c82f2e8fc5" />
<img width="585" height="1176" alt="Screenshot 2026-04-22 171240" src="https://github.com/user-attachments/assets/6a76f577-d12d-4ddf-8f80-23790ec228f8" />
<img width="560" height="1154" alt="Screenshot 2026-04-22 170810" src="https://github.com/user-attachments/assets/6d0e98ec-ffac-463e-b6f9-b78e5283951a" />
<img width="1058" height="1314" alt="Screenshot 2026-04-22 170732" src="https://github.com/user-attachments/assets/dbf06e10-2e2b-43f6-a2c3-b6bd810edcd6" />

## ⚙️ How to Run the Project

1. Clone repository:
https://github.com/damianzawidzki/DZCoffee-App.git

2. Open in Android Studio

3. Connect Firebase:
- Add google-services.json
- Enable Authentication and Firestore

4. Run the app on emulator or real device

---

## 📈 Future Improvements

- Online payments integration  
- Push notifications  
- Dark mode  
- Image upload instead of URL  
- Real-time order tracking  
- AI recommendations  

---

## 📚 Academic Value

This project demonstrates:
- Mobile application development lifecycle  
- Clean architecture (MVVM)  
- Database integration (Firestore)  
- Role-based access control  
- Real-world business logic (café ordering system)  

---

## 👨‍💻 Author

Damian Zawidzki  
Software Engineering Student  
De Montfort University  

---

## 📌 Notes

- This project is created for portfolio and academic purposes  
- Some features are simplified for demonstration  

This project is for educational purposes.
