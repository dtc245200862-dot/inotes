<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iNotes - Dashboard Quản lý Ghi chú</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .navbar-brand {
            font-weight: 700;
            letter-spacing: 0.5px;
        }
        .storage-card {
            background: linear-gradient(135deg, #ffffff, #f1f5f9);
            border-left: 5px solid #0d6efd;
            box-shadow: 0 4px 6px rgba(0,0,0,0.05);
        }
        .storage-card.file-mode {
            border-left-color: #ffc107;
        }
        .table-hover tbody tr:hover {
            background-color: #f1f7fe;
        }
        .badge-type-canhan { background-color: #0dcaf0; color: #000; }
        .badge-type-congviec { background-color: #0d6efd; color: #fff; }
        .badge-type-hoctap { background-color: #198754; color: #fff; }
        .note-title-link {
            font-weight: 600;
            color: #212529;
            text-decoration: none;
        }
        .note-title-link:hover {
            color: #0d6efd;
            text-decoration: underline;
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/notes">
            <i class="fa-solid fa-note-sticky text-warning me-2"></i>iNotes
        </a>
        <span class="navbar-text text-light small d-none d-md-inline">
            Ứng dụng ghi chú kiến trúc Strategy Pattern
        </span>
    </div>
</nav>

<div class="container py-4">

    <!-- Flash Messages -->
    <c:if test="${param.message == 'created'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="fa-solid fa-circle-check me-2"></i>Thêm mới ghi chú thành công!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.message == 'updated'}">
        <div class="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
            <i class="fa-solid fa-circle-info me-2"></i>Cập nhật ghi chú thành công!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.message == 'deleted'}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="fa-solid fa-trash-can me-2"></i>Đã xóa ghi chú khỏi hệ thống!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.message == 'storage_switched'}">
        <div class="alert alert-primary alert-dismissible fade show shadow-sm" role="alert">
            <i class="fa-solid fa-arrows-rotate me-2"></i>Đã chuyển đổi thành công tầng lưu trữ dữ liệu (Strategy Pluggable Storage)!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Strategy Pattern Runtime Switcher Box -->
    <div class="card p-3 mb-4 storage-card ${currentStorageKey == 'FILE' ? 'file-mode' : ''}">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
            <div>
                <h6 class="mb-1 text-muted text-uppercase fw-bold small">
                    <i class="fa-solid fa-layer-group me-1"></i>Tầng Lưu Trữ Hiện Tại (Strategy Pattern)
                </h6>
                <div class="d-flex align-items-center gap-2">
                    <c:choose>
                        <c:when test="${currentStorageKey == 'DB'}">
                            <span class="badge bg-primary fs-6 px-3 py-2">
                                <i class="fa-solid fa-database me-2"></i>Cơ sở dữ liệu (MySQL inotes_db)
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning text-dark fs-6 px-3 py-2">
                                <i class="fa-solid fa-file-lines me-2"></i>Tập tin văn bản (File System inotes_data.txt)
                            </span>
                        </c:otherwise>
                    </c:choose>
                    <span class="text-muted small">| Hệ thống cho phép hoán đổi tầng lưu trữ tức thì lúc Runtime</span>
                </div>
            </div>
            <div>
                <c:choose>
                    <c:when test="${currentStorageKey == 'DB'}">
                        <a href="${pageContext.request.contextPath}/notes?action=switchStorage&targetStorage=FILE" 
                           class="btn btn-outline-warning text-dark fw-semibold shadow-sm">
                            <i class="fa-solid fa-file-code me-1"></i>Chuyển sang Lưu trữ Tập tin (File)
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/notes?action=switchStorage&targetStorage=DB" 
                           class="btn btn-outline-primary fw-semibold shadow-sm">
                            <i class="fa-solid fa-database me-1"></i>Chuyển sang Cơ sở dữ liệu (MySQL)
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Header Actions & Search Form -->
    <div class="card shadow-sm p-3 mb-4">
        <div class="row g-3 align-items-center justify-content-between">
            <div class="col-md-7">
                <form action="${pageContext.request.contextPath}/notes" method="get" class="d-flex gap-2">
                    <input type="hidden" name="action" value="search">
                    <div class="input-group">
                        <span class="input-group-text bg-white"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                        <input type="text" class="form-control" name="keyword" value="${keyword}" 
                               placeholder="Tìm kiếm theo tiêu đề hoặc nội dung ghi chú...">
                        <button class="btn btn-primary" type="submit">Tìm kiếm</button>
                    </div>
                    <c:if test="${not empty keyword}">
                        <a href="${pageContext.request.contextPath}/notes" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-rotate-left"></i>
                        </a>
                    </c:if>
                </form>
            </div>
            <div class="col-md-auto text-end">
                <a href="${pageContext.request.contextPath}/notes?action=create" class="btn btn-success shadow-sm">
                    <i class="fa-solid fa-plus me-1"></i>Thêm Mới Ghi Chú
                </a>
            </div>
        </div>
    </div>

    <!-- Main Notes Table (Bước 5: iNotes Dashboard) -->
    <div class="card shadow-sm">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold">
                <i class="fa-solid fa-list-check text-primary me-2"></i>Danh Sách Ghi Chú (iNotes Dashboard)
            </h5>
            <span class="badge bg-secondary">Tổng số: ${listNotes.size()} ghi chú</span>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 5%;" class="text-center">#</th>
                        <th style="width: 35%;">Tiêu đề</th>
                        <th style="width: 15%;">Phân loại</th>
                        <th style="width: 25%;">Nội dung tóm tắt</th>
                        <th style="width: 20%;" class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty listNotes}">
                            <tr>
                                <td colspan="5" class="text-center py-4 text-muted">
                                    <i class="fa-regular fa-folder-open fs-1 mb-2 d-block text-secondary"></i>
                                    Không có ghi chú nào được tìm thấy trong kho lưu trữ hiện tại.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="note" items="${listNotes}" varStatus="status">
                                <tr>
                                    <td class="text-center fw-bold text-muted">${status.count}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/notes?action=view&id=${note.id}" class="note-title-link">
                                            ${note.title}
                                        </a>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${note.typeId == 1}">
                                                <span class="badge badge-type-canhan"><i class="fa-solid fa-user me-1"></i>${note.typeName != null ? note.typeName : 'Cá nhân'}</span>
                                            </c:when>
                                            <c:when test="${note.typeId == 2}">
                                                <span class="badge badge-type-congviec"><i class="fa-solid fa-briefcase me-1"></i>${note.typeName != null ? note.typeName : 'Công việc'}</span>
                                            </c:when>
                                            <c:when test="${note.typeId == 3}">
                                                <span class="badge badge-type-hoctap"><i class="fa-solid fa-graduation-cap me-1"></i>${note.typeName != null ? note.typeName : 'Học tập'}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${note.typeName != null ? note.typeName : 'Khác'}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-muted small text-truncate" style="max-width: 250px;">
                                        ${note.content}
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/notes?action=view&id=${note.id}" 
                                               class="btn btn-outline-info" title="Xem chi tiết">
                                                <i class="fa-solid fa-eye me-1"></i>Xem
                                            </a>
                                            <a href="${pageContext.request.contextPath}/notes?action=edit&id=${note.id}" 
                                               class="btn btn-outline-primary" title="Sửa ghi chú">
                                                <i class="fa-solid fa-pen-to-square me-1"></i>Sửa
                                            </a>
                                            <button type="button" class="btn btn-outline-danger" 
                                                    onclick="confirmDelete(${note.id}, '${note.title}')" title="Xóa ghi chú">
                                                <i class="fa-solid fa-trash me-1"></i>Xóa
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title"><i class="fa-solid fa-triangle-exclamation me-2"></i>Xác nhận xóa ghi chú</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Bạn có chắc chắn muốn xóa ghi chú: <strong id="deleteNoteTitle"></strong>?
                <p class="text-danger small mt-2 mb-0">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy bỏ</button>
                <a id="confirmDeleteBtn" href="#" class="btn btn-danger">Xóa Vĩnh Viễn</a>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(id, title) {
        document.getElementById('deleteNoteTitle').innerText = title;
        document.getElementById('confirmDeleteBtn').href = '${pageContext.request.contextPath}/notes?action=delete&id=' + id;
        var myModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        myModal.show();
    }
</script>
</body>
</html>
