# 📘 Hướng dẫn Cài đặt, Kiểm thử & Báo cáo kết quả API (MySQL)

Dự án: `contact-selection-api` (Spring Boot + MySQL)

---

##  1. Cài đặt môi trường

### 📋 Yêu cầu hệ thống
*   **Java (JDK)**: Phiên bản 21.0.x (Khuyên dùng OpenJDK hoặc GraalVM).
*   **Maven**: Phiên bản 3.9 trở lên.
*   **MySQL Server**: Phiên bản 8.0 trở lên.
*   **IDE**: VS Code (cài thêm pack Java) hoặc IntelliJ IDEA.

---

### � Hướng dẫn cho macOS (Dùng Homebrew)

1.  **Cài đặt MySQL & Maven**:
    ```bash
    brew install mysql maven
    brew services start mysql
    ```
2.  **Thiết lập Database**:
    *   Mật khẩu mặc định tại source là 'Admin@123'

    *   Mở Terminal, truy cập MySQL (user `root`, mặc định chưa có pass nếu cài mới):
        ```bash
        mysql -u root
        # Trong MySQL:
        CREATE DATABASE purchase_db CHARACTER SET utf8mb4;
        ALTER USER 'root'@'localhost' IDENTIFIED BY 'Admin@123';
        FLUSH PRIVILEGES;
        EXIT;
        ```
    *   Import dữ liệu:
        ```bash
        mysql -u root -pAdmin@123 purchase_db < src/main/resources/db/schema_mysql.sql
        ```
3.  **Khởi chạy API**:
    ```bash
    # Chỉ định Java 21 nếu máy cài nhiều bản Java 
    JAVA_HOME=/path/to/java/21 mvn spring-boot:run -DskipTests 
    ```

---

### 🪟 Hướng dẫn cho Windows

1.  **Cài đặt MySQL**: 
    *   Tải [MySQL Installer](https://dev.mysql.com/downloads/installer/) và chọn bản **Community Server**.
    *   Trong cấu hình, đặt mật khẩu `root` là `Admin@123`.
2.  **Cài đặt Java & Maven**:
    *   Tải JDK 21 (Microsoft hoặc Oracle).
    *   Cài đặt Maven và cấu hình **Environment Variables** (PATH) cho cả Java và Maven.
3.  **Thiết lập Database**:
    *   Mở **MySQL Command Line Client**.
    *   Chạy câu lệnh tạo DB như trên macOS.
    *   Sử dụng lệnh `source` để import file sql:
        ```sql
        USE purchase_db;
        SOURCE C:\path\to\project\src\main\resources\db\schema_mysql.sql;
        ```
4.  **Khởi chạy API**: 
    Mở CMD hoặc PowerShell tại thư mục projec:
    ```cmd
    mvn spring-boot:run -DskipTests
    ```
    Hoặc (Automation Test)
    ```cmd
    mvn spring-boot:run 
    ```
---

## 2. Kịch bản kiểm thử (Test Cases)

👉 **URL Giao diện Swagger**: [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)

### Kịch bản 1: Mở màn hình - Load danh sách khu vực
- **Mô tả**: Lấy danh sách 7 vùng miền (rgon_m) để hiển thị các checkbox trên UI.
- **Endpoint**: `POST /api/purchase/event_entry/event_info/ajax/ref_select_load`
- **Request Body**:
```json
{
  "kindRef": 0
}
```
- **Response trả về (Success)**:
```json
{
  "success": true,
  "message": "初期表示完了",
  "data": {
    "rgonInfoList": [
      {
        "rgonCd": 1,
        "rgonNm": "北海道・東北"
      },
      {
        "rgonCd": 2,
        "rgonNm": "関東"
      },
      {
        "rgonCd": 3,
        "rgonNm": "中部"
      },
      {
        "rgonCd": 4,
        "rgonNm": "近畿"
      },
      {
        "rgonCd": 5,
        "rgonNm": "中国・四国"
      },
      {
        "rgonCd": 6,
        "rgonNm": "九州・沖縄"
      },
      {
        "rgonCd": 7,
        "rgonNm": "海外"
      }
    ]
  },
  "errorCode": null
}
```

---

### Kịch bản 2: Tìm kiếm thành công (Kết quả bình thường ≤ 80)
- **Mô tả**: Tìm kiếm theo tên nơi liên hệ và nhận về danh sách dữ liệu ngay lập tức.
- **Endpoint**: `POST /api/purchase/event_entry/event_info/ajax/ref_select_search`
- **Request Body**:
```json
{
  "kindRef": 0,
  "txtRefNm": "東京",
  "confirmed": false
}
```
- **Response trả về (Success)**:
```json
{
  "success": true,
  "data": {
    "totalCount": 1,
    "needsConfirmation": false,
    "refList": [
      {
        "refCd": "REF001",
        "refNm": "東京チケットセンター",
        "telno": "03-1234-5678",
        "prefecture": "東京都",
        "rgonNm": "関東",
        "urlDisplay": "https://tokyo.ticket.example.com / https://tokyo.ticket.example.com/en"
      }
    ]
  }
}
```

---

### Kịch bản 3: Tìm kiếm vượt ngưỡng 80 record (Cảnh báo)
- **Mô tả**: Hệ thống đếm được 101 bản ghi (> 80) nên dừng lại để hỏi ý kiến người dùng.
- **Request Body** (confirmed là false):
```json
{
  "kindRef": 0,
  "confirmed": false
}
```
- **Response trả về (Cảnh báo)**:
```json
{
  "success": true,
  "data": {
    "totalCount": 101,
    "needsConfirmation": true,
    "refList": null
  }
}
```

---

### Kịch bản 4: Tìm kiếm vượt ngưỡng 80 record (Sau khi xác nhận)
- **Mô tả**: Người chọn nhấn "Yes" (Đồng ý xem tiếp), UI gửi lại request với `confirmed: true`.
- **Request Body** (confirmed là true):
```json
{
  "kindRef": 0,
  "confirmed": true
}
```
- **Response trả về (Đầy đủ dữ liệu)**:
```json
{
  "success": true,
  "data": {
    "totalCount": 101,
    "needsConfirmation": false,
    "refList": [ ... 101 records ... ]
  }
}
```

---

### Kịch bản 5: Không tìm thấy kết quả (No Result)
- **Mô tả**: Tìm theo từ khóa không tồn tại.
- **Request Body**:
```json
{
  "kindRef": 0,
  "txtRefNm": "KHÔNG_TỒN_TẠI"
}
```
- **Response trả về (Lỗi 404)**:
```json
{
  "success": false,
  "message": "Không có giá trị phù hợp với điều kiện tìm kiếm.",
  "data": null,
  "errorCode": "MSG_NO_RESULT"
}
```

---

### Kịch bản 6: Lỗi Validation (Thiếu thông tin bắt buộc)
- **Mô tả**: Gửi request mà không có trường `kindRef` (Trường bắt buộc theo thiết kế).
- **Request Body**:
```json
{
  "txtRefNm": "test"
}
```
- **Response trả về (Lỗi 400)**:
```json
{
  "success": false,
  "message": "Validation thất bại: kindRef: kindRef là bắt buộc",
  "data": null,
  "errorCode": "MSG_VALIDATION"
}
```

---


