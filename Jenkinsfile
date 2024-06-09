pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                echo "Starting Build Stage"
				sh '''./gradlew build clean'''
                echo "The build stage passed..."
            }
        }
    }
}