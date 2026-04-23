# 🍽️ Food Order REST API

Food Order REST API adalah project backend sederhana yang dibuat untuk mensimulasikan alur pemesanan makanan secara online.  
Di dalam project ini, user bisa register dan login, melihat menu, menambahkan menu ke cart, melakukan checkout, melihat riwayat pesanan, dan untuk admin juga tersedia fitur laporan penjualan.

Project ini dibuat menggunakan **Java Spring Boot** dan fokus pada implementasi backend yang rapi, aman, dan realistis untuk kebutuhan latihan, tugas, maupun portfolio dasar backend developer.

---

## ✨ What this project can do

### 🔐 Authentication
- User bisa **register** sebagai customer
- User bisa **login** dan mendapatkan **JWT token**
- Password disimpan dengan aman menggunakan **BCrypt**
- Sistem memiliki 2 role:
  - **ADMIN**
  - **CUSTOMER**

---

### 🍜 Menu
#### Admin bisa:
- Melihat daftar menu
- Menambahkan menu baru
- Mengupdate menu
- Menghapus menu

#### Customer bisa:
- Melihat daftar menu
- Mencari menu berdasarkan nama atau kategori
- Melihat daftar menu dengan pagination

Data menu yang digunakan meliputi:
- `id`
- `name`
- `description`
- `price`
- `category`
- `stock`

---

### 🛒 Cart
Customer bisa:
- Melihat isi cart
- Menambahkan item ke cart
- Mengubah jumlah item di cart
- Menghapus item dari cart

---

### ✅ Checkout Order
Saat customer checkout:
- sistem akan memvalidasi stok terlebih dahulu
- data cart akan diproses menjadi order
- stok menu akan berkurang
- cart akan dikosongkan setelah transaksi berhasil

---

### 📜 Order History
Customer bisa melihat:
- daftar pesanan yang pernah dibuat
- detail item di setiap order

---

### 📊 Sales Report
Admin bisa melihat:
- total penjualan
- total order
- menu yang paling sering terjual
- laporan berdasarkan filter harian dan bulanan

---

## 🎁 Bonus Features
Selain fitur utama, project ini juga punya beberapa bonus fitur:
- Search menu berdasarkan **nama** dan **kategori**
- Pagination untuk daftar menu
- API documentation dengan **Swagger UI**
- Dockerization dengan **Docker** dan **Docker Compose**

---

## 🧰 Tech Stack

Project ini dibangun menggunakan:

- **Java 21**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **JWT**
- **MySQL**
- **Maven**
- **Swagger / OpenAPI**
- **Docker & Docker Compose**

---

## 📁 Project Structure

```bash
src/main/java/com/bootcamp/foodorder
├── controller       # Handle HTTP request
├── service          # Business logic
├── repository       # Database access dengan JPA
├── entity           # Mapping entity ke tabel database
├── dto              # Request & response object
├── config           # Security, JWT, Swagger config
├── util             # Helper / utility class
└── FoodorderApplication.java
```
---

### 🛡️ Role Access
## 👨‍💼 ADMIN

Admin bisa:
- **melihat menu**
- **menambahkan menu**
- **mengupdate menu**
- **menghapus menu**
- **melihat laporan penjualan**

## 👤 CUSTOMER

Customer bisa:
- **melihat menu**
- **menambahkan item ke cart**
- **mengelola cart**
- **checkout order**
- **melihat riwayat pesanan**

---

## 🚀 Main Endpoints
# Authentication
- **```POST /auth/register``` → register customer baru**
- **```POST /auth/login``` → login user**

# Menu
- **```GET /menus``` → lihat daftar menu**
- **```POST /menus``` → tambah menu (Admin only)**
- **```PUT /menus/{id}``` → update menu (Admin only)**
- **```DELETE /menus/{id}``` → hapus menu (Admin only)***

# Cart
- **```GET /cart``` → lihat isi cart (Customer only)***
- **```POST /cart``` → tambah item ke cart (Customer only)***
- **```PUT /cart/{id}``` → update quantity item (Customer only)**
- **```DELETE /cart/{id}``` → hapus item dari cart (Customer only)**

# Order
- **```POST /orders/checkout``` → checkout cart menjadi order (Customer only)**
- **```GET /orders``` → lihat order history (Customer only)**

# Reports
- **```GET /reports/sales``` → lihat rekap penjualan (Admin only)**

---

##🔎 Search & Pagination

Endpoint GET /menus mendukung query parameter berikut:
- **```name```→ filter berdasarkan nama menu**
- **```category``` → filter berdasarkan kategori menu**
- **```page``` → nomor halaman**
- **```size``` → jumlah data per halaman**

contoh penggunaan: 
```bash
GET /menus
GET /menus?name=nasi
GET /menus?category=Drink
GET /menus?name=nasi&category=Main Course
GET /menus?page=0&size=5
GET /menus?name=nasi&page=0&size=5
```
---

## 📘 API Documentation
Dokumentasi API tersedia melalui Swagger UI:
```bash
http://localhost:8080/swagger-ui.html
```
Swagger ini bisa dipakai untuk:

- **melihat daftar endpoint**
- **mencoba request langsung**
- **testing endpoint yang memakai JWT lewat tombol Authorize**

---

## ⚙️ Run Locally

1. Clone repository

```bash
git clone https://github.com/USERNAME/REPOSITORY_NAME.git
cd REPOSITORY_NAME
```
2. Buat database MySQL
```bash
CREATE DATABASE IF NOT EXISTS food_order_db;
```
3. Sesuaikan ```application.properties```
- contoh konfigurasi:
```bash
server.port=8080

spring.application.name=foodorder

spring.datasource.url=jdbc:mysql://localhost:3306/food_order_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=YOUR_SECRET_KEY
jwt.expiration=86400000

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.api-docs.path=/v3/api-docs
```
4. Jalankan aplikasi
```bash
mvnw.cmd spring-boot:run
```

---

## 🐳 Run with Docker
Project ini juga sudah bisa dijalankan menggunakan Docker.

Build dan run
```bash
docker compose up --build
```

Stop container
```bash
docker compose down
```
Setelah berhasil dijalankan, aplikasi bisa diakses di: 
```bash
http://localhost:8080/swagger-ui.html
```

---

## 🗃️ Database Design
Entity utama yang digunakan:

- **User**
- **Menu**
- **CartItem**
- **Order**
- **OrderItem**

Relasi sederhananya:
- **1 user bisa penya banyak cart item**
- **1 user bisa punya banyak order**
- **1 order bisa punya banyak order item**
- **1 menu bisa muncul di cart item dan order item**

---

## 🧪 Testing Flow
Project ini sudah diuji dengan alur utama seperti:

Admin Flow
- **login admin**
- **tambah menu**
- **update menu**
- **lihat daftar menu**
- **lihat sales report**

Customer flow
- **register customer**
- **login customer**
- **lihat menu**
- **tambah item ke cart**
- **update cart**
- **checkout order**
- **lihat order history**

Negative testing
- **login dengan password salah**
- **customer mencoba akses endpoint admin**
- **quantity melebihi stok**
- **checkout saat cart kosong**
- **validasi input yang tidak sesuai**

---

## 🎯 Purpose of this project
Project ini dibuat untuk melatih dan menunjukkan implementasi backend yang mencakup:

- **CRUD API**
- **authentication & authorization**
- **validasi input**
- **relasi database**
- **API Documentation**
- **contrainerization dengan Docker**

---

## 👨‍💻 Author

Developed by Muhammad Arsyad Giri

---
