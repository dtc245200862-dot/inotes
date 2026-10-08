<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iNotes - Thêm Mới Ghi Chú</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/notes">
            <i class="fa-solid fa-note-sticky text-warning me-2"></i>iNotes
        </a>
    </div>
</nav>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h5 class="card-title mb-0 fw-bold">
                        <i class="fa-solid fa-file-circle-plus me-2"></i>Thêm Mới Ghi Chú
                    </h5>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <span class="badge bg-secondary">
                            Đang lưu vào: <strong>${currentStrategyName}</strong>
                        </span>
                    </div>

                    <form action="${pageContext.request.contextPath}/notes" method="post">
                        <input type="hidden" name="action" value="insert">

                        <!-- Tiêu đề (text input) -->
                        <div class="mb-3">
                            <label for="title" class="form-label fw-semibold">
                                Tiêu đề: <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control form-control-lg" id="title" name="title" 
                                   placeholder="Nhập tiêu đề ghi chú..." required autofocus>
                        </div>

                        <!-- Phân loại (select dropdown) -->
                        <div class="mb-3">
                            <label for="typeId" class="form-label fw-semibold">
                                Phân loại: <span class="text-danger">*</span>
                            </label>
                            <select class="form-select form-select-lg" id="typeId" name="typeId" required>
                                <option value="" disabled selected>-- Chọn thể loại ghi chú --</option>
                                <c:forEach var="t" items="${listTypes}">
                                    <option value="${t.id}">${t.name} - ${t.description}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Nội dung (textarea) -->
                        <div class="mb-4">
                            <label for="content" class="form-label fw-semibold">
                                Nội dung: <span class="text-danger">*</span>
                            </label>
                            <textarea class="form-control" id="content" name="content" rows="6" 
                                      placeholder="Nhập nội dung chi tiết của ghi chú..." required></textarea>
                        </div>

                        <!-- Nút Lưu & Hủy -->
                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/notes" class="btn btn-secondary px-4">
                                <i class="fa-solid fa-xmark me-1"></i>Hủy bỏ
                            </a>
                            <button type="submit" class="btn btn-success px-4 fw-semibold shadow-sm">
                                <i class="fa-solid fa-floppy-disk me-1"></i>Lưu Ghi Chú
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
