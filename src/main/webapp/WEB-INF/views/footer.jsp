<%--
    Footer Include File for USMS
    
    This file contains the common footer section
    that is included in all JSP pages.
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    </div> <!-- End of Main Content Container -->
    
    <!-- Footer Section -->
    <footer class="footer text-center">
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <h5>University Student Management System (USMS)</h5>
                    <p class="mb-0">
                        A Spring MVC Web Application demonstrating software construction and development principles.
                    </p>
                    <hr style="border-color: rgba(255,255,255,0.3);">
                    <p class="mb-1">
                        <strong>Team Members:</strong><br>
                        Muhammad Kashan Tariq (212145) | Adeel Hussain (221829)<br>
                        Syed Abdain (221855) | Muhammad Tauseef (221789)
                    </p>
                    <p class="mb-0 text-muted">
                        © 2025 Software Construction and Development - All Rights Reserved
                    </p>
                </div>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap 5 JavaScript Bundle from CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JavaScript for interactive features -->
    <script>
        // Confirm before deleting a student
        function confirmDelete(id, name) {
            if (confirm('Are you sure you want to delete student: ' + name + '?')) {
                window.location.href = '${pageContext.request.contextPath}/students/delete/' + id;
            }
        }
        
        // Auto-hide alerts after 5 seconds
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    alert.classList.add('fade');
                    alert.classList.remove('show');
                });
            }, 5000);
        });
    </script>
</body>
</html>
