<%--
    Student View JSP Page
    
    This page displays detailed information about a single student.
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
--%>

<%@ include file="header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Student Details</span>
                <span class="badge bg-secondary">ID: ${student.id}</span>
            </div>
            <div class="card-body">
                <div class="text-center mb-4">
                    <div class="avatar-circle mx-auto mb-3" style="width: 80px; height: 80px; background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 32px; font-weight: bold;">
                        ${fn:substring(student.name, 0, 1)}
                    </div>
                    <h3>${student.name}</h3>
                    <p class="text-muted">${student.course}</p>
                </div>
                
                <hr>
                
                <div class="row mb-3">
                    <div class="col-4 text-muted">Student ID:</div>
                    <div class="col-8 fw-bold">${student.id}</div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-4 text-muted">Full Name:</div>
                    <div class="col-8 fw-bold">${student.name}</div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-4 text-muted">Email:</div>
                    <div class="col-8">
                        <a href="mailto:${student.email}" class="text-decoration-none">
                            ${student.email}
                        </a>
                    </div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-4 text-muted">Course:</div>
                    <div class="col-8">
                        <span class="badge bg-info text-dark">${student.course}</span>
                    </div>
                </div>
                
                <c:if test="${not empty student.phone}">
                    <div class="row mb-3">
                        <div class="col-4 text-muted">Phone:</div>
                        <div class="col-8">
                            <a href="tel:${student.phone}" class="text-decoration-none">
                                ${student.phone}
                            </a>
                        </div>
                    </div>
                </c:if>
                
                <c:if test="${not empty student.address}">
                    <div class="row mb-3">
                        <div class="col-4 text-muted">Address:</div>
                        <div class="col-8">${student.address}</div>
                    </div>
                </c:if>
                
                <hr>
                
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/students/edit/${student.id}" 
                       class="btn btn-warning btn-lg">
                        Edit Student
                    </a>
                    <button onclick="confirmDelete(${student.id}, '${student.name}')" 
                            class="btn btn-danger btn-lg">
                        Delete Student
                    </button>
                    <a href="${pageContext.request.contextPath}/students" 
                       class="btn btn-outline-secondary btn-lg">
                        Back to List
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>