<%--
    Index JSP Page - Landing Page
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
--%>

<%@ include file="WEB-INF/views/header.jsp" %>

<div class="text-center mb-5">
    <h1 class="display-4 fw-bold text-primary">University Student Management System</h1>
    <p class="lead text-muted">A Spring MVC Web Application for Managing Student Records</p>
</div>

<div class="row g-4 mb-5">
    <div class="col-md-4">
        <div class="card h-100 text-center">
            <div class="card-body">
                <h5 class="card-title">Add Students</h5>
                <p class="card-text">Register new students with their personal and academic information.</p>
                <a href="${pageContext.request.contextPath}/students/add" class="btn btn-primary">Add Student</a>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 text-center">
            <div class="card-body">
                <h5 class="card-title">View All Students</h5>
                <p class="card-text">Browse and manage all registered student records in one place.</p>
                <a href="${pageContext.request.contextPath}/students" class="btn btn-primary">View Students</a>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 text-center">
            <div class="card-body">
                <h5 class="card-title">Search Students</h5>
                <p class="card-text">Quickly find students by name.</p>
                <form class="d-flex" action="${pageContext.request.contextPath}/students/search" method="GET">
                    <input class="form-control me-2" type="search" name="keyword" placeholder="Search..." required>
                    <button class="btn btn-primary" type="submit">Search</button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-body text-center">
        <h5 class="card-title">Development Team</h5>
        <p class="card-text">This project was developed by:</p>
        <div class="row mt-4">
            <div class="col-md-3 col-6 mb-3">
                <div class="p-3 border rounded">
                    <strong>Muhammad Kashan Tariq</strong><br>
                    <small class="text-muted">Reg#: 212145</small>
                </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
                <div class="p-3 border rounded">
                    <strong>Adeel Hussain</strong><br>
                    <small class="text-muted">Reg#: 221829</small>
                </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
                <div class="p-3 border rounded">
                    <strong>Syed Abdain</strong><br>
                    <small class="text-muted">Reg#: 221855</small>
                </div>
            </div>
            <div class="col-md-3 col-6 mb-3">
                <div class="p-3 border rounded">
                    <strong>Muhammad Tauseef</strong><br>
                    <small class="text-muted">Reg#: 221789</small>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="WEB-INF/views/footer.jsp" %>