pipeline {
    agent any
    stages {
		stage('Checkout code'){
			steps {
				checkout scm
			}
		}
        stage('build') {
            steps {
                echo "Starting Build Stage"
				sh '''./gradlew build clean'''
                echo "The build stage passed..."
            }
        }
    }
}