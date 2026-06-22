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

Add your screenshots into a folder called "screens" and update paths if needed:

<img width="568" height="1187" alt="Screenshot 2026-04-22 171411" src="https://github.com/user-attachments/assets/0436cd00-26dc-407c-b64f-33f2fe4e6d05" />
<img width="565" height="1177" alt="Screenshot 2026-04-22 171748" src="https://github.com/user-attachments/assets/66204ba5-6a4a-4213-beb3-c797c81ce35d" />
<img width="577" height="1197" alt="Screenshot 2026-04-22 171716" src="https://github.com/user-attachments/assets/39cd0a4d-24de-4a93-931b-7a1d12d76dd3" />
<img width="568" height="1192" alt="Screenshot 2026-04-22 171650" src="https://github.com/user-attachments/assets/2f019401-b37d-45c2-b4bb-bdc1fcd6ae1d" />
<img width="578" height="1189" alt="Screenshot 2026-04-22 171505" src="https://github.com/user-attachments/assets/ac4cf9dc-3a3c-4933-9eeb-0a58c114e788" />
<img width="625" height="1231" alt="Screenshot 2026-04-22 171442" src="https://github.com/user-attachments/assets/f66497f1-7436-4841-a275-44ec2502d267" />
<img width="585" height="1176" alt="Screenshot 2026-04-22 171240" src="https://github.com/user-attachments/assets/217fb820-0e8b-4ab9-8337-00e76539d7ef" />
<img width="601" height="1187" alt="Screenshot 2026-04-22 171331" src="https://github.com/user-attachments/assets/a02ecaa7-a83e-46ba-8f29-6ed05f63d43a" />
<img width="570" height="1180" alt="Screenshot 2026-04-22 171313" src="https://github.com/user-attachments/assets/9d21362e-0abb-44cf-a2a6-0644093addc8" />
<img width="600" height="1195" alt="Screenshot 2026-04-22 171254" src="https://github.com/user-attachments/assets/56edf416-c6a8-4c4d-9cf1-531ffa702839" />
<img width="560" height="1154" alt="Screenshot 2026-04-22 170810" src="https://github.com/user-attachments/assets/4d6f7c64-c2be-4d58-9792-961f1160e9db" />
<img width="1058" height="1314" alt="Screenshot 2026-04-22 170732" src="https://github.com/user-attachments/assets/03b34cb1-3f82-4434-8c09-b62851184050" />
<img width="566" height="1187" alt="Screenshot 2026-04-22 171811" src="https://github.com/user-attachments/assets/6390e4c8-d1df-448b-b9c9-ba0e18da8e10" />
<img width="570" height="1180" alt="Screenshot 2026-04-22 172006" src="https://github.com/user-attachments/assets/d1f5249b-f518-4316-8e15-14fa45095139" />
<img width="535" height="1180" alt="Screenshot 2026-04-22 171945" src="https://github.com/user-attachments/assets/503d8abe-f0fe-444e-ab89-791f268af076" />
<img width="589" height="1180" alt="Screenshot 2026-04-22 171927" src="https://github.com/user-attachments/assets/a5b0ff53-c1da-49f1-887c-b073e4e246a9" />
<img width="552" height="1175" alt="Screenshot 2026-04-22 171843" src="https://github.com/user-attachments/assets/bab70faf-e128-4ba0-a406-781e4aba21c5" />
<img width="602" height="1197" alt="Screenshot 2026-04-22 171829" src="https://github.com/user-attachments/assets/181ca7c8-14d6-4708-93da-d749b0c7bc7d" />



---

## ⚙️ How to Run the Project

1. Clone repository:
git clone https://github.com/pepik13/CafeApp.git

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
