# Minimenu Backend

Bu proje, Android tabanlı Minimenu uygulamasının sunucu tarafını oluşturur.  
Uygulama restoran menüsünü listeleme, ürün görüntüleme, sepet oluşturma ve sipariş işlemlerini yönetir.  
Backend kısmı Java Spring Boot kullanılarak geliştirilmiştir.

---

## 1. Genel Bilgi

Bu proje, mobil uygulamanın API isteklerini karşılayan bir RESTful servis sağlar.  
Ürün, kategori ve sipariş verileri veritabanı üzerinden yönetilir.  
Ayrıca ürün görselleri `src/main/resources/static/images/` dizininden servis edilir.

Backend, Postman veya Android uygulaması üzerinden test edilebilir.

---

## 2. Kullanılan Teknolojiler

**Dil ve Çerçeveler**
- Java 17
- Spring Boot 3.x
- Spring Web (REST API geliştirme)
- Spring Data JPA (veri erişimi)
- Hibernate ORM

**Veritabanı**
- H2 (geliştirme ortamında gömülü)
- MySQL (isteğe bağlı olarak yapılandırılabilir)

**Araçlar ve Kütüphaneler**
- Maven veya Gradle (proje derleme aracı)
- Lombok (isteğe bağlı, model sınıflarında getter/setter kısaltma)
- Postman (API test aracı)
- IntelliJ IDEA (IDE)
- Git ve GitHub (versiyon kontrol)

---

## 3. Proje Klasör Yapısı
src/
├── main/
│ ├── java/com/minimenu/app/
│ │ ├── controller/ -> API uç noktaları (Category, Product, Order)
│ │ ├── dto/ -> Veri transfer objeleri (Request/Response modelleri)
│ │ ├── entity/ -> Veritabanı varlıkları (Category, Product, OrderItem vb.)
│ │ ├── repository/ -> JPA repository sınıfları (CRUD işlemleri)
│ │ └── service/ -> İş mantığı (business logic)
│ │
│ └── resources/
│ ├── static/images/ -> Ürün resimleri (örnek: burger.png, cola.png)
│ ├── templates/ -> Opsiyonel HTML şablonları
│ └── application.properties -> Sunucu ve veritabanı ayarları
│
└── test/ -> Birim testleri (isteğe bağlı)

## 4. Projenin Amacı

- Restoran menüsünü mobil uygulamaya JSON formatında sunmak
- Ürünleri kategorilere göre filtrelemek
- Kullanıcının seçtiği ürünlerle sipariş oluşturmak
- Sipariş verilerini veritabanında saklamak
- Ürün görsellerini URL yoluyla frontend tarafına iletmek


Controller → Dış dünyaya açılan REST endpoint’ler
Service → İş mantığı ve veri işlemleri
Repository → Veritabanı CRUD işlemleri
Entity → Veri modelleri
DTO → API’de istek ve cevap modelleri

Proje Geliştirme Süreci
Backend projesi IntelliJ IDEA kullanılarak oluşturuldu.
Spring Boot üzerinden temel yapı kuruldu (Controller, Service, Repository).
H2 veritabanı ile hızlı geliştirme ortamı sağlandı.
Postman kullanılarak tüm endpoint’ler test edildi.
Görseller static/images dizininden servis edildi.
Android uygulaması Retrofit aracılığıyla bu backend'e bağlandı.

