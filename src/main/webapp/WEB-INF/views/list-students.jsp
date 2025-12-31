<%--
    List Students JSP Page
    
    This page displays all student records in a responsive table format.
    Users can view, edit, and delete student records from this page.
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
--%>

<%@ include file="header.jsp" %>

<div class="row mb-4">
    <div class="col-md-8">
        <h2 class="text-primary">📋 Student List</h2>
        <p class="text-muted">Manage all registered students</p>
    </div>
    <div class="col-md-4 text-end">
        <a href="${pageContext.request.contextPath}/students/add" class="btn btn-primary btn-lg">
            ➕ Add New Student
        </a>
    </div>
</div>

<!-- Success/Error Messages -->
<c:if test="${not empty message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <strong>Success!</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <strong>Error!</strong> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Search Results Info -->
<c:if test="${not empty keyword}">
    <div class="alert alert-info">
        Showing results for: <strong>"${keyword}"</strong>
        <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-outline-info ms-2">Show All</a>
    </div>
</c:if>

<!-- Statistics Cards -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card text-center">
            <div class="card-body">
                <h3 class="card-title text-primary">${fn:length(students)}</h3>
                <p class="card-text text-muted">Total Students</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center">
            <div class="card-body">
                <h3 class="card-title text-success">
                    ${fn:length(students)}
                </h3>
                <p class="card-text text-muted">Active Records</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center">
            <div class="card-body">
                <h3 class="card-title text-warning">
                    <c:set var="courses" value="${fn:length(students)}" />
                    ${courses > 0 ? 'Active' : 'N/A'}
                </h3>
                <p class="card-text text-muted">This Month</p>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center">
            <div class="card-body">
                <h3 class="card-title text-info">100%</h3>
                <p class="card-text text-muted">Data Integrity</p>
            </div>
        </div>
    </div>
</div>

<!-- Students Table -->
<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <span>📊 Student Records</span>
        <span class="badge bg-light text-dark">${fn:length(students)} students</span>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty students}">
                <div class="text-center py-5">
                    <img src="https://cdn-icons-png.flaticon.com/512/748/748137.png" alt="No students" width="100" class="mb-3">
                    <h4 class="text-muted">No students found</h4>
                    <p class="text-muted">Add your first student to get started!</p>
                    <a href="${pageContext.request.contextPath}/students/add" class="btn btn-primary">
                        ➕ Add First Student
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th class="text-center" style="width: 10%;">ID</th>
                                <th style="width: 25%;">Name</th>
                                <th style="width: 25%;">Email</th>
                                <th style="width: 20%;">Course</th>
                                <th class="text-center" style="width: 20%;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="student" items="${students}">
                                <tr>
                                    <td class="text-center">
                                        <span class="badge bg-secondary">${student.id}</span>
                                    </td>
                                    <td>
                                        <strong>${student.name}</strong>
                                    </td>
                                    <td>
                                        <a href="mailto:${student.email}" class="text-decoration-none">
                                            📧 ${student.email}
                                        </a>
                                    </td>
                                    <td>
                                        <span class="badge bg-info text-dark">${student.course}</span>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/students/view/${student.id}" 
                                           class="btn btn-sm btn-outline-primary action-btn" title="View">
                                            👁️
                                        </a>
                                        <a href="${pageContext.request.contextPath}/students/edit/${student.id}" 
                                           class="btn btn-sm btn-outline-warning action-btn" title="Edit">
                                            ✏️
                                        </a>
                                        <button onclick="confirmDelete(${student.id}, '${student.name}')" 
                                                class="btn btn-sm btn-outline-danger action-btn" title="Delete">
                                            🗑️
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="footer.jsp" %>
