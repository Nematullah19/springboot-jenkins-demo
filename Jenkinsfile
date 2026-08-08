pipeline {

    agent any

    options {
        skipDefaultCheckout(true)
    }

    stages {

        stage('Checkout') {
            steps {
                deleteDir()

                echo 'Downloading source code from GitHub...'

                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh '''
                    echo "===================================="
                    echo "JENKINS ENVIRONMENT"
                    echo "===================================="

                    echo "Workspace:"
                    pwd

                    echo "Running as user:"
                    whoami

                    echo "Java version:"
                    java -version

                    echo "Maven version:"
                    mvn -version

                    echo "Files downloaded from GitHub:"
                    ls -la
                '''
            }
        }

        stage('Clean') {
            steps {
                echo 'Cleaning previous Maven build...'

                sh 'mvn -B clean'
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Building and testing Spring Boot project...'

                sh 'mvn -B package'
            }
        }

        stage('Verify JAR') {
            steps {
                echo 'Checking generated JAR...'

                sh 'ls -lh target/*.jar'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    echo "===================================="
                    echo "DEPLOYING SPRING BOOT APPLICATION"
                    echo "===================================="

                    cp target/jenkins-demo-0.0.1-SNAPSHOT.jar \
                       /opt/springboot-jenkins-demo-deploy/app.jar.new

                    mv /opt/springboot-jenkins-demo-deploy/app.jar.new \
                       /opt/springboot-jenkins-demo-deploy/app.jar

                    sudo /usr/bin/systemctl restart jenkins-demo.service
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Checking Spring Boot service..."

                    sudo /usr/bin/systemctl is-active jenkins-demo.service

                    i=1

                    while [ "$i" -le 15 ]
                    do

                        if curl -fsS http://127.0.0.1:8081/api/message
                        then
                            echo ""
                            echo "===================================="
                            echo "APPLICATION HEALTH CHECK PASSED"
                            echo "===================================="
                            exit 0
                        fi

                        echo "Application not ready yet..."
                        echo "Waiting 2 seconds..."

                        sleep 2

                        i=$((i + 1))

                    done

                    echo "APPLICATION HEALTH CHECK FAILED"

                    exit 1
                '''
            }
        }

    }

    post {

        success {
            echo 'BUILD AND DEPLOYMENT SUCCESSFUL'
        }

        failure {
            echo 'BUILD OR DEPLOYMENT FAILED'
        }

    }

}
