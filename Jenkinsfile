/*
    Jenkinsfile - CI/CD Pipeline for University Student Management System
    
    This Jenkinsfile defines the complete CI/CD pipeline for building,
    testing, and deploying the USMS application.
    
    Pipeline Stages:
    1. Checkout - Pull code from GitHub
    2. Build - Compile Java source code
    3. Test - Run unit tests
    4. Package - Create WAR file
    5. Deploy - Deploy to Tomcat
    
    Team Members:
    - Muhammad Kashan Tariq (212145)
    - Adeel Hussain (221829)
    - Syed Abdain (221855)
    - Muhammad Tauseef (221789)
*/

pipeline {
    // Agent configuration - can be 'any' or specific label
    agent any
    
    // Environment variables for the pipeline
    environment {
        // Application configuration
        APP_NAME = 'USMS'
        APP_VERSION = '1.0.0'
        
        // Database configuration
        DB_NAME = 'usms_db'
        DB_USER = 'usms_user'
        DB_PASS = credentials('mysql-password')
        
        // Tomcat configuration
        TOMCAT_URL = 'http://localhost:8080'
        TOMCAT_USER = credentials('tomcat-user')
        TOMCAT_PASS = credentials('tomcat-password')
    }
    
    // Pipeline options
    options {
        // Timeout for the entire pipeline
        timeout(time: 30, unit: 'MINUTES')
        
        // Build discarder - keep last 5 builds
        buildDiscarder(logRotator(numToKeepStr: '5'))
        
        // Disable concurrent builds
        disableConcurrentBuilds()
        
        // Checkout to a subdirectory
        checkoutToSubdirectory('workspace')
    }
    
    // Define parameters for manual deployment
    parameters {
        choice(
            name: 'DEPLOY_ENVIRONMENT',
            choices: ['dev', 'test', 'prod'],
            description: 'Select deployment environment'
        )
        booleanParam(
            name: 'RUN_TESTS',
            defaultValue: true,
            description: 'Run unit tests before building'
        )
    }
    
    // Pipeline stages
    stages {
        // Stage 1: Checkout Code
        stage('Checkout') {
            steps {
                echo '=========================================='
                echo 'Stage 1: Checking out code from GitHub...'
                echo '=========================================='
                
                // Clean workspace before checkout
                cleanWs()
                
                // Checkout code from GitHub
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/yourusername/USMS_Project.git',
                        credentialsId: 'github-credentials'
                    ]]
                ])
                
                echo 'Code checkout completed successfully!'
            }
        }
        
        // Stage 2: Build (Compile)
        stage('Build') {
            steps {
                echo '=========================================='
                echo 'Stage 2: Building application...'
                echo '=========================================='
                
                // Display Java version
                sh 'java -version'
                
                // Run Maven clean compile
                sh 'mvn clean compile -DskipTests'
                
                echo 'Build completed successfully!'
            }
        }
        
        // Stage 3: Test
        stage('Test') {
            when {
                expression { params.RUN_TESTS }
            }
            steps {
                echo '=========================================='
                echo 'Stage 3: Running unit tests...'
                echo '=========================================='
                
                // Run Maven tests
                sh 'mvn test'
                
                // Publish JUnit test results
                junit '**/target/surefire-reports/*.xml'
                
                // Collect code coverage (if configured)
                step([$class: 'JUnitPublisher', testResults: '**/target/surefire-reports/*.xml'])
                
                echo 'All tests passed successfully!'
            }
            post {
                always {
                    // Archive test results
                    archiveArtifacts artifacts: '**/target/surefire-reports/*.html', allowEmptyArchive: true
                }
                failure {
                    echo 'Tests failed! Please check the test reports.'
                }
            }
        }
        
        // Stage 4: Package
        stage('Package') {
            steps {
                echo '=========================================='
                echo 'Stage 4: Packaging application...'
                echo '=========================================='
                
                // Run Maven package (creates WAR file)
                sh 'mvn package -DskipTests'
                
                // Rename WAR file with version
                sh 'mv target/USMS.war target/USMS-${APP_VERSION}.war'
                
                // Archive WAR file
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                
                echo 'Application packaged successfully!'
            }
        }
        
        // Stage 5: Deploy to Tomcat
        stage('Deploy') {
            when {
                expression { params.DEPLOY_ENVIRONMENT == 'dev' }
            }
            steps {
                echo '=========================================='
                echo 'Stage 5: Deploying to Tomcat...'
                echo '=========================================='
                
                // Deploy WAR file to Tomcat
                deploy adapters: [
                    tomcat9(
                        credentialsId: 'tomcat-credentials',
                        url: 'http://localhost:8080'
                    )
                ], 
                war: 'target/*.war', 
                onFailure: 'ABORT'
                
                echo 'Application deployed successfully!'
            }
        }
        
        // Stage 6: Integration Test (Optional)
        stage('Integration Test') {
            when {
                expression { params.DEPLOY_ENVIRONMENT != 'prod' }
            }
            steps {
                echo '=========================================='
                echo 'Stage 6: Running integration tests...'
                echo '=========================================='
                
                // Wait for application to start
                sleep(time: 10, unit: 'SECONDS')
                
                // Check if application is running
                sh 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/USMS/'
                    .trim() == '200' || error('Application not responding')
                
                echo 'Integration tests passed!'
            }
        }
    }
    
    // Post-build actions
    post {
        always {
            echo '=========================================='
            echo 'Pipeline execution completed!'
            echo '=========================================='
            
            // Send email notification (optional)
            emailext(
                subject: "Build ${currentBuild.result}: ${JOB_NAME}",
                body: """<p>Build completed with status: ${currentBuild.result}</p>
                        <p>Check console output at: ${BUILD_URL}</p>""",
                recipientProviders: [[$class: 'RequesterRecipientProvider']]
            )
        }
        
        success {
            echo '🎉 Build and deployment successful!'
            // Add notification logic here
        }
        
        failure {
            echo '❌ Build failed! Please check the logs.'
            // Add notification logic here
        }
        
        unstable {
            echo '⚠️ Build is unstable. Please investigate.'
        }
    }
}

/*
    Jenkins Setup Instructions:
    
    1. Install Required Jenkins Plugins:
       - Git Plugin
       - Maven Integration Plugin
       - Pipeline Plugin
       - JUnit Plugin
       - Deploy to Container Plugin
       - Email Extension Plugin
    
    2. Configure Jenkins:
       - Go to Manage Jenkins → Global Tool Configuration
       - Configure JDK (point to JDK 17 installation)
       - Configure Maven (Maven 3.8+)
    
    3. Configure Credentials:
       - Go to Manage Jenkins → Manage Credentials
       - Add GitHub credentials
       - Add Tomcat credentials
       - Add MySQL credentials
    
    4. Create Jenkins Job:
       - New Item → Pipeline
       - Paste this Jenkinsfile or point to GitHub repo
       - Save and run
    
    5. Trigger Builds:
       - Manual trigger from Jenkins UI
       - Webhook trigger from GitHub (on push)
       - Scheduled builds using poll SCM
*/
