# FashionStore - API Documentation

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Customer API Endpoints](#customer-api-endpoints)
4. [Admin API Endpoints](#admin-api-endpoints)
5. [Data Models](#data-models)
6. [Error Responses](#error-responses)
7. [Rate Limiting](#rate-limiting)

---

## Overview

The FashionStore platform provides two distinct API interfaces:

1. **Customer API**: Traditional servlet endpoints serving JSP views and handling form submissions
2. **Admin API**: RESTful JSON API under `/api/admin/*` for the React admin dashboard

### Base URLs

- **Customer API**: `http://localhost:8080/FashionStore`
- **Admin API**: `http://localhost:8080/FashionStore/api/admin`

### Authentication

Both APIs use session-based authentication. A valid session is required for most endpoints.

**CSRF Protection**: All state-changing requests (POST, PUT, DELETE) must include a valid CSRF token in either:
- Header: `X-CSRF-Token`
- Form parameter: `csrfToken`

---

## Authentication

### Login

**Endpoint**: `POST /login`

**Request Body**:
```
email: string (required)
password: string (required)
```

**Response**: Redirects to `/home` on success, renders login page with error on failure

**Example**:
```bash
curl -X POST http://localhost:8080/FashionStore/login \
  -d "email=admin@fashionstore.com" \
  -d "password=admin123" \
  -H "X-CSRF-Token: your-csrf-token"
```

### Logout

**Endpoint**: `POST /logout`

**Response**: Redirects to `/login`

**Example**:
```bash
curl -X POST http://localhost:8080/FashionStore/logout \
  -H "X-CSRF-Token: your-csrf-token"
```

### Register

**Endpoint**: `POST /register`

**Request Body**:
```
fullName: string (required)
email: string (required, unique)
phone: string (optional)
password: string (required, min 8 chars)
address: string (optional)
gender: string (optional: Male, Female, Other)
```

**Response**: Redirects to `/home` on success, renders registration page with error on failure

### Admin Login

**Endpoint**: `POST /api/admin/login`

**Request Body** (JSON):
```json
{
  "email": "admin@fashionstore.com",
  "password": "admin123"
}
```

**Response** (JSON):
```json
{
  "userId": 1,
  "fullName": "Admin User",
  "email": "admin@fashionstore.com",
  "role": "admin"
}
```

---

## Customer API Endpoints

### Home

**Endpoint**: `GET /home`

**Description**: Renders the homepage with featured products, categories, and trending items

**Response**: HTML (JSP rendered)

### Products

**Endpoint**: `GET /products`

**Query Parameters**:
- `category`: Filter by category slug
- `tag`: Filter by tag (new, sale, trending)
- `minPrice`: Minimum price filter
- `maxPrice`: Maximum price filter
- `size`: Filter by size (can be multiple)
- `sortBy`: Sort order (price_asc, price_desc, popularity, newest)
- `page`: Page number (default: 1)

**Response**: HTML (JSP rendered)

**Example**:
```
GET /products?category=men&minPrice=500&maxPrice=2000&sortBy=price_asc&page=1
```

### Product Details

**Endpoint**: `GET /product`

**Query Parameters**:
- `id`: Product ID (required)

**Response**: HTML (JSP rendered)

**Example**:
```
GET /product?id=1
```

### Search

**Endpoint**: `GET /search`

**Query Parameters**:
- `q`: Search query (required)
- `page`: Page number (default: 1)

**Response**: HTML (JSP rendered)

**Example**:
```
GET /search?q=shirt&page=1
```

### Search Suggestions

**Endpoint**: `GET /api/search/suggestions`

**Query Parameters**:
- `q`: Search query (required)

**Response** (JSON):
```json
["Cotton Shirt", "Silk Blouse", "Linen Tunic"]
```

### Cart

**Add to Cart**: `POST /cart`

**Request Body**:
```
action: "add"
productId: integer (required)
size: string (required)
quantity: integer (default: 1)
```

**Update Cart Item**: `POST /cart`

**Request Body**:
```
action: "update"
cartItemId: integer (required)
quantity: integer (required)
```

**Remove from Cart**: `POST /cart`

**Request Body**:
```
action: "remove"
cartItemId: integer (required)
```

**View Cart**: `GET /cart`

**Response**: HTML (JSP rendered)

### Wishlist

**Add to Wishlist**: `POST /wishlist`

**Request Body**:
```
action: "add"
productId: integer (required)
```

**Remove from Wishlist**: `POST /wishlist`

**Request Body**:
```
action: "remove"
productId: integer (required)
```

**Move to Cart**: `POST /wishlist`

**Request Body**:
```
action: "moveToCart"
productId: integer (required)
```

**View Wishlist**: `GET /wishlist`

**Response**: HTML (JSP rendered)

### Checkout

**Initiate Checkout**: `GET /checkout`

**Response**: HTML (JSP rendered)

**Place Order**: `POST /checkout`

**Request Body**:
```
addressId: integer (required)
paymentMethod: string (required: card, upi, cod)
couponCode: string (optional)
```

**Response**: Redirects to `/success` or renders payment failure page

### Orders

**View Order History**: `GET /orders`

**Response**: HTML (JSP rendered)

**View Order Details**: `GET /orders`

**Query Parameters**:
- `id`: Order ID (required)

**Response**: HTML (JSP rendered)

### Address Management

**Add Address**: `POST /account/addresses`

**Request Body**:
```
action: "add"
fullName: string (required)
addressLine1: string (required)
addressLine2: string (optional)
city: string (required)
state: string (required)
zipCode: string (required)
country: string (required)
phone: string (required)
```

**Delete Address**: `POST /account/addresses`

**Request Body**:
```
action: "delete"
addressId: integer (required)
```

**Set Default Address**: `POST /account/addresses`

**Request Body**:
```
action: "setDefault"
addressId: integer (required)
```

### Reviews

**Submit Review**: `POST /reviews`

**Request Body**:
```
productId: integer (required)
rating: integer (required, 1-5)
reviewText: string (required)
```

**View Product Reviews**: `GET /reviews`

**Query Parameters**:
- `productId`: Product ID (required)

**Response**: HTML (JSP rendered)

---

## Admin API Endpoints

### Current User

**Endpoint**: `GET /api/admin/me`

**Description**: Get current authenticated admin user

**Response** (JSON):
```json
{
  "userId": 1,
  "fullName": "Admin User",
  "email": "admin@fashionstore.com",
  "role": "admin"
}
```

### Dashboard

**Endpoint**: `GET /api/admin/dashboard`

**Response** (JSON):
```json
{
  "totalRevenue": 150000.00,
  "totalOrders": 500,
  "totalUsers": 200,
  "activeProducts": 150,
  "lowStockCount": 5,
  "pendingOrders": 10,
  "recentOrders": [...],
  "recentUsers": [...],
  "topProducts": [...]
}
```

### Stats

**Endpoint**: `GET /api/admin/stats`

**Response** (JSON):
```json
{
  "totalRevenue": 150000.00,
  "totalOrders": 500,
  "totalUsers": 200,
  "activeProducts": 150,
  "lowStockCount": 5,
  "pendingOrders": 10
}
```

### Products

**List Products**: `GET /api/admin/products`

**Query Parameters**:
- `page`: Page number (default: 1)
- `limit`: Items per page (default: 20)
- `search`: Search query
- `category`: Filter by category
- `active`: Filter by active status

**Response** (JSON):
```json
{
  "products": [
    {
      "productId": 1,
      "productName": "Cotton Shirt",
      "price": 899.00,
      "discountPercent": 10.00,
      "categoryName": "Men",
      "stockQuantity": 50,
      "active": true,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalCount": 150,
  "currentPage": 1,
  "totalPages": 8
}
```

**Create Product**: `POST /api/admin/products`

**Request Body** (JSON):
```json
{
  "productName": "Cotton Shirt",
  "description": "Premium cotton shirt",
  "price": 899.00,
  "discountPercent": 10.00,
  "categoryId": 1,
  "brand": "FashionStore",
  "imageUrl": "https://example.com/image.jpg",
  "sizes": [
    {"sizeLabel": "S", "stockQuantity": 20},
    {"sizeLabel": "M", "stockQuantity": 30},
    {"sizeLabel": "L", "stockQuantity": 25}
  ],
  "isNew": true,
  "isSale": false,
  "isTrending": false
}
```

**Response** (JSON):
```json
{
  "success": true,
  "productId": 1
}
```

**Get Product**: `GET /api/admin/products/{id}`

**Response** (JSON):
```json
{
  "productId": 1,
  "productName": "Cotton Shirt",
  "description": "Premium cotton shirt",
  "price": 899.00,
  "discountPercent": 10.00,
  "categoryId": 1,
  "brand": "FashionStore",
  "imageUrl": "https://example.com/image.jpg",
  "active": true,
  "isNew": true,
  "isSale": false,
  "isTrending": false,
  "sizes": [
    {"sizeId": 1, "sizeLabel": "S", "stockQuantity": 20},
    {"sizeId": 2, "sizeLabel": "M", "stockQuantity": 30},
    {"sizeId": 3, "sizeLabel": "L", "stockQuantity": 25}
  ]
}
```

**Update Product**: `PUT /api/admin/products/{id}`

**Request Body** (JSON): Same as create product

**Response** (JSON):
```json
{
  "success": true
}
```

**Delete Product**: `DELETE /api/admin/products/{id}`

**Response** (JSON):
```json
{
  "success": true
}
```

### Orders

**List Orders**: `GET /api/admin/orders`

**Query Parameters**:
- `page`: Page number (default: 1)
- `limit`: Items per page (default: 20)
- `status`: Filter by status
- `search`: Search by order ID or email

**Response** (JSON):
```json
{
  "orders": [
    {
      "orderId": 1,
      "userId": 10,
      "fullName": "John Doe",
      "email": "john@example.com",
      "totalAmount": 1798.00,
      "status": "Processing",
      "paymentStatus": "Paid",
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalCount": 500,
  "currentPage": 1,
  "totalPages": 25
}
```

**Get Order**: `GET /api/admin/orders/{id}`

**Response** (JSON):
```json
{
  "orderId": 1,
  "userId": 10,
  "fullName": "John Doe",
  "email": "john@example.com",
  "shippingAddress": {...},
  "totalAmount": 1798.00,
  "discountAmount": 0.00,
  "finalAmount": 1798.00,
  "status": "Processing",
  "paymentMethod": "card",
  "paymentStatus": "Paid",
  "orderItems": [...],
  "createdAt": "2024-01-01T00:00:00"
}
```

**Update Order Status**: `PUT /api/admin/orders/{id}/status`

**Request Body** (JSON):
```json
{
  "status": "Shipped"
}
```

**Response** (JSON):
```json
{
  "success": true
}
```

**Recent Orders**: `GET /api/admin/orders/recent`

**Response** (JSON):
```json
[
  {
    "orderId": 1,
    "fullName": "John Doe",
    "totalAmount": 1798.00,
    "status": "Processing",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### Users

**List Users**: `GET /api/admin/users`

**Query Parameters**:
- `page`: Page number (default: 1)
- `limit`: Items per page (default: 20)
- `search`: Search by name or email
- `role`: Filter by role

**Response** (JSON):
```json
{
  "users": [
    {
      "userId": 1,
      "fullName": "John Doe",
      "email": "john@example.com",
      "role": "customer",
      "active": true,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalCount": 200,
  "currentPage": 1,
  "totalPages": 10
}
```

**Update User Role**: `PUT /api/admin/users/{id}/role`

**Request Body** (JSON):
```json
{
  "role": "admin"
}
```

**Response** (JSON):
```json
{
  "success": true
}
```

**Recent Users**: `GET /api/admin/users/recent`

**Response** (JSON):
```json
[
  {
    "userId": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "customer",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### Inventory

**View Inventory**: `GET /api/admin/inventory`

**Query Parameters**:
- `page`: Page number (default: 1)
- `limit`: Items per page (default: 20)
- `lowStock`: Filter by low stock (threshold: 10)

**Response** (JSON):
```json
{
  "inventory": [
    {
      "productId": 1,
      "productName": "Cotton Shirt",
      "sizeLabel": "S",
      "stockQuantity": 5,
      "categoryName": "Men"
    }
  ],
  "totalCount": 50,
  "currentPage": 1,
  "totalPages": 3
}
```

**Low Stock**: `GET /api/admin/inventory/low-stock`

**Query Parameters**:
- `threshold`: Stock threshold (default: 10)

**Response** (JSON):
```json
[
  {
    "productId": 1,
    "productName": "Cotton Shirt",
    "sizeLabel": "S",
    "stockQuantity": 5
  }
]
```

### Categories

**List Categories**: `GET /api/admin/categories`

**Response** (JSON):
```json
[
  {
    "categoryId": 1,
    "categoryName": "Men",
    "categorySlug": "men",
    "description": "Men's fashion",
    "active": true
  }
]
```

**Create Category**: `POST /api/admin/categories`

**Request Body** (JSON):
```json
{
  "categoryName": "Accessories",
  "categorySlug": "accessories",
  "description": "Fashion accessories"
}
```

**Response** (JSON):
```json
{
  "success": true,
  "categoryId": 5
}
```

**Update Category**: `PUT /api/admin/categories/{id}`

**Request Body** (JSON): Same as create category

**Response** (JSON):
```json
{
  "success": true
}
```

**Delete Category**: `DELETE /api/admin/categories/{id}`

**Response** (JSON):
```json
{
  "success": true
}
```

### Coupons

**List Coupons**: `GET /api/admin/coupons`

**Response** (JSON):
```json
[
  {
    "couponId": 1,
    "couponCode": "SAVE20",
    "discountType": "percentage",
    "discountValue": 20.00,
    "minOrderValue": 500.00,
    "usageLimit": 100,
    "expiryDate": "2024-12-31",
    "active": true
  }
]
```

**Create Coupon**: `POST /api/admin/coupons`

**Request Body** (JSON):
```json
{
  "couponCode": "SAVE20",
  "discountType": "percentage",
  "discountValue": 20.00,
  "minOrderValue": 500.00,
  "usageLimit": 100,
  "expiryDate": "2024-12-31"
}
```

**Response** (JSON):
```json
{
  "success": true,
  "couponId": 1
}
```

### Analytics

**Sales Analytics**: `GET /api/admin/analytics/sales`

**Query Parameters**:
- `period`: Time period (daily, weekly, monthly)

**Response** (JSON):
```json
{
  "revenueOverTime": [
    {"date": "2024-01-01", "revenue": 5000.00},
    {"date": "2024-01-02", "revenue": 6000.00}
  ],
  "ordersByStatus": {
    "Pending": 10,
    "Processing": 20,
    "Shipped": 15,
    "Delivered": 455
  },
  "topProducts": [...],
  "salesByCategory": [...]
}
```

---

## Data Models

### User

```json
{
  "userId": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+91-9876543210",
  "gender": "Male",
  "address": "123 Main St",
  "role": "customer",
  "active": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

### Product

```json
{
  "productId": 1,
  "productName": "Cotton Shirt",
  "description": "Premium cotton shirt",
  "price": 899.00,
  "discountPercent": 10.00,
  "imageUrl": "https://example.com/image.jpg",
  "categoryId": 1,
  "categoryName": "Men",
  "brand": "FashionStore",
  "active": true,
  "isNew": true,
  "isSale": false,
  "isTrending": false,
  "sizes": [
    {"sizeLabel": "S", "stockQuantity": 20},
    {"sizeLabel": "M", "stockQuantity": 30}
  ],
  "createdAt": "2024-01-01T00:00:00"
}
```

### Order

```json
{
  "orderId": 1,
  "userId": 10,
  "fullName": "John Doe",
  "email": "john@example.com",
  "shippingAddress": {
    "addressLine1": "123 Main St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400001",
    "country": "India"
  },
  "totalAmount": 1798.00,
  "discountAmount": 0.00,
  "finalAmount": 1798.00,
  "status": "Processing",
  "paymentMethod": "card",
  "paymentStatus": "Paid",
  "orderItems": [...],
  "createdAt": "2024-01-01T00:00:00"
}
```

### OrderItem

```json
{
  "orderItemId": 1,
  "orderId": 1,
  "productId": 1,
  "productName": "Cotton Shirt",
  "size": "M",
  "quantity": 2,
  "price": 899.00,
  "discountPercent": 10.00,
  "totalPrice": 1618.20
}
```

---

## Error Responses

### Error Response Format

```json
{
  "error": "Error message"
}
```

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

### Common Errors

**401 Unauthorized**:
```json
{
  "error": "Unauthorized"
}
```

**403 Forbidden**:
```json
{
  "error": "Forbidden"
}
```

**404 Not Found**:
```json
{
  "error": "Endpoint not found"
}
```

**500 Internal Server Error**:
```json
{
  "error": "Internal server error"
}
```

---

## Rate Limiting

Rate limiting is configured to prevent API abuse:

- **Customer API**: 100 requests per minute per IP
- **Admin API**: 200 requests per minute per IP

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1609459200
```

When rate limit is exceeded:
```json
{
  "error": "Rate limit exceeded"
}
```
HTTP Status: 429 Too Many Requests
