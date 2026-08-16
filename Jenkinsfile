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
                      .
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                sh '''
                    docker rm -f springboot-jenkins-demo || true

                    docker run -d \
                      --name springboot-jenkins-demo \
                      -p 8082:8081 \
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
                        if curl -fsS http://127.0.0.1:8082/api/status
                        then
                            exit 0
                        fi

                        sleep 2
                        i=$((i + 1))
                    done

                    exit 1
                '''
            }
        }

    }

}
