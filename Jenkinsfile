pipeline {

    agent any

    stages {

        stage('Build and Test') {
            steps {
                sh 'mvn -B clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build \
                      -t springboot-jenkins-demo:${BUILD_NUMBER} \
                      -t springboot-jenkins-demo:latest \
                      .
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                sh '''
                    docker rm -f springboot-jenkins-demo \
                      >/dev/null 2>&1 || true

                    docker run -d \
                      --name springboot-jenkins-demo \
                      -p 8081:8081 \
                      springboot-jenkins-demo:${BUILD_NUMBER}
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    i=1

                    while [ "$i" -le 15 ]
                    do
                        if curl -fsS http://127.0.0.1:8081/api/status
                        then
                            echo ""
                            echo "Docker container is healthy."
                            exit 0
                        fi

                        echo "Waiting for Spring Boot container..."
                        sleep 2

                        i=$((i + 1))
                    done

                    echo "Application health check failed."

                    docker logs \
                      --tail 100 \
                      springboot-jenkins-demo

                    exit 1
                '''
            }
        }

    }

}
