pipeline {
    agent any
    stages {
		stage('tmp'){
			steps {
				echo "Testing... Testing... 123..."
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