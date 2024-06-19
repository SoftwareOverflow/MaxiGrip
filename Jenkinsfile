pipeline {
    agent any
    stages {
/*		stage('Checkout code'){
			steps {
				checkout scm
			}
		}*/
		stage('Copy Files'){
		    steps {
		        sh '''
		        cp "C:/android/jenkinsFiles/MaxiGrip/google-services.json" ./app
                cp "C:/android/jenkinsFiles/MaxiGrip/local.properties" .
		        '''
		    }
		}
        stage('build') {
            steps {
                echo "Starting Clean Stage"
				sh '''./gradlew clean'''
                echo "The clean stage passed..."
                sh '''./gradlew build'''
                echo "The build stage passed..."
            }
        }
        stage ('Test'){
			steps {
				sh '''./gradlew testDebugUnitTest'''
			}
		}
		stage('Create Release & Tag'){
			steps {
			    script {
					def versionCode = sh(
						script: 'cat app/build.gradle | grep versionCode | awk \'{print $2}\'',
						returnStdout: true
					).trim()
					def versionName = sh(
						script: 'cat app/build.gradle | grep versionName | awk \'{print $2}\'',
						returnStdout: true
					).trim()
					echo "VERSION CODE: ${versionCode}"
			    }
			}
		}
    }
}