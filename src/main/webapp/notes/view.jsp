<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iNotes - Chi Tiết Ghi Chú</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .content-box {
            background-color: #ffffff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            white-space: pre-wrap;
            line-height: 1.7;
            font-size: 1.05rem;
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
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0 fw-bold text-primary">
                        <i class="fa-solid fa-eye me-2"></i>Chi Tiết Ghi Chú #${note.id}
                    </h5>
                    <span class="badge bg-secondary">Tầng: ${currentStrategyName}</span>
                </div>
                <div class="card-body p-4">
                    <h3 class="fw-bold mb-3 text-dark">${note.title}</h3>

                    <div class="d-flex align-items-center gap-3 mb-4 text-muted small border-bottom pb-3">
                        <div>
                            <i class="fa-solid fa-tag me-1 text-primary"></i>Phân loại: 
                            <span class="badge bg-primary">${note.typeName != null ? note.typeName : 'Chưa phân loại'}</span>
                        </div>
                        <div>
                            <i class="fa-regular fa-clock me-1 text-secondary"></i>Thời gian: 
                            <span>${note.createdAtFormatted}</span>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold text-secondary">Nội dung ghi chú:</label>
                        <div class="content-box text-dark shadow-sm">
                            <c:out value="${note.content}" />
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center pt-2">
                        <a href="${pageContext.request.contextPath}/notes" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left me-1"></i>Quay lại danh sách
                        </a>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/notes?action=edit&id=${note.id}" class="btn btn-primary">
                                <i class="fa-solid fa-pen-to-square me-1"></i>Sửa ghi chú
                            </a>
                            <a href="${pageContext.request.contextPath}/notes?action=delete&id=${note.id}" 
                               class="btn btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa ghi chú này?');">
                                <i class="fa-solid fa-trash me-1"></i>Xóa ghi chú
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
