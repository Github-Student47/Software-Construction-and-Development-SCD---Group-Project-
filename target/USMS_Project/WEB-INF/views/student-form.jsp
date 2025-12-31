<%--
    Student Form JSP Page
    
    This page is used for both adding new students and editing existing ones.
    The form collects student information including name, email, course, phone, and address.
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
--%>

<%@ include file="header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                ${action == 'Update' ? '✏️ Edit Student' : '➕ Add New Student'}
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/students/save" 
                      method="POST" class="needs-validation" novalidate>
                
                    <!-- Hidden field for student ID (used in edit mode) -->
                    <input type="hidden" name="id" value="${student.id}">
                    
                    <!-- Student Name -->
                    <div class="mb-3">
                        <label for="name" class="form-label fw-bold">
                            👤 Student Name <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${student.name}" required
                               placeholder="Enter full name">
                        <div class="invalid-feedback">
                            Please enter the student's name.
                        </div>
                    </div>
                    
                    <!-- Email -->
                    <div class="mb-3">
                        <label for="email" class="form-label fw-bold">
                            📧 Email Address <span class="text-danger">*</span>
                        </label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${student.email}" required
                               placeholder="Enter email address">
                        <div class="invalid-feedback">
                            Please enter a valid email address.
                        </div>
                    </div>
                    
                    <!-- Course -->
                    <div class="mb-3">
                        <label for="course" class="form-label fw-bold">
                            📚 Course <span class="text-danger">*</span>
                        </label>
                        <select class="form-select" id="course" name="course" required>
                            <option value="">Select a course</option>
                            <option value="Computer Science" ${student.course == 'Computer Science' ? 'selected' : ''}>
                                Computer Science
                            </option>
                            <option value="Software Engineering" ${student.course == 'Software Engineering' ? 'selected' : ''}>
                                Software Engineering
                            </option>
                            <option value="Information Technology" ${student.course == 'Information Technology' ? 'selected' : ''}>
                                Information Technology
                            </option>
                            <option value="Data Science" ${student.course == 'Data Science' ? 'selected' : ''}>
                                Data Science
                            </option>
                            <option value="Artificial Intelligence" ${student.course == 'Artificial Intelligence' ? 'selected' : ''}>
                                Artificial Intelligence
                            </option>
                            <option value="Cyber Security" ${student.course == 'Cyber Security' ? 'selected' : ''}>
                                Cyber Security
                            </option>
                            <option value="Business Administration" ${student.course == 'Business Administration' ? 'selected' : ''}>
                                Business Administration
                            </option>
                            <option value="Mathematics" ${student.course == 'Mathematics' ? 'selected' : ''}>
                                Mathematics
                            </option>
                            <option value="Physics" ${student.course == 'Physics' ? 'selected' : ''}>
                                Physics
                            </option>
                            <option value="Other" ${student.course == 'Other' ? 'selected' : ''}>
                                Other
                            </option>
                        </select>
                        <div class="invalid-feedback">
                            Please select a course.
                        </div>
                    </div>
                    
                    <!-- Phone (Optional) -->
                    <div class="mb-3">
                        <label for="phone" class="form-label fw-bold">
                            📱 Phone Number (Optional)
                        </label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               value="${student.phone}"
                               placeholder="Enter phone number">
                    </div>
                    
                    <!-- Address (Optional) -->
                    <div class="mb-4">
                        <label for="address" class="form-label fw-bold">
                            🏠 Address (Optional)
                        </label>
                        <textarea class="form-control" id="address" name="address" rows="2"
                                  placeholder="Enter address">${student.address}</textarea>
                    </div>
                    
                    <!-- Form Buttons -->
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary btn-lg">
                            ${action == 'Update' ? '✅ Update Student' : '✅ Save Student'}
                        </button>
                        <a href="${pageContext.request.contextPath}/students" 
                           class="btn btn-outline-secondary btn-lg">
                            ❌ Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Help Card -->
        <div class="card mt-4">
            <div class="card-body">
                <h5 class="card-title">💡 Instructions</h5>
                <ul class="mb-0">
                    <li>Fields marked with <span class="text-danger">*</span> are required.</li>
                    <li>Please enter a valid email address for communication.</li>
                    <li>Select the appropriate course from the dropdown.</li>
                    <li>Phone and address are optional but recommended.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Form Validation Script -->
<script>
    // Enable Bootstrap form validation
    (function() {
        'use strict';
        
        // Fetch all forms with the 'needs-validation' class
        var forms = document.querySelectorAll('.needs-validation');
        
        // Loop over them and prevent submission
        Array.prototype.slice.call(forms)
            .forEach(function(form) {
                form.addEventListener('submit', function(event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
    })();
</script>

<%@ include file="footer.jsp" %>
