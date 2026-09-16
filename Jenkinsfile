pipeline {

    agent any

    options {
        disableConcurrentBuilds()
    }

    stages {

        stage('Build and Test') {
            steps {
                sh '''
                    echo "=== CI: Maven Build and Tests ==="
                    mvn -B clean package
                '''
            }
        }


        stage('Build Docker Image') {
            steps {
                sh '''
                    echo "=== CI: Build Docker Image ==="

                    docker build \
                      -t springboot-jenkins-demo:${BUILD_NUMBER} \
                      .
                '''
            }
        }


        stage('Candidate HSM Test') {
            steps {
                sh '''
                    echo "=== CD: Start Candidate Container ==="

                    docker rm -f springboot-hsm-candidate \
                      2>/dev/null || true

                    docker run -d \
                      --name springboot-hsm-candidate \
                      --env-file /etc/hsm-lab/hsm.env \
                      --group-add 985 \
                      -p 8084:8081 \
                      -v /etc/hsm-lab/softhsm.cfg:/etc/hsm-lab/softhsm.cfg:ro \
                      -v /etc/softhsm/softhsm2.conf:/etc/softhsm/softhsm2.conf:ro \
                      -v /var/lib/softhsm/tokens:/var/lib/softhsm/tokens:rw \
                      springboot-jenkins-demo:${BUILD_NUMBER}

                    i=1

                    while [ "$i" -le 15 ]
                    do
                        echo "Candidate health check attempt $i..."

                        APP_OK=0
                        HSM_OK=0

                        if curl -fsS \
                          http://127.0.0.1:8084/api/status \
                          >/dev/null
                        then
                            APP_OK=1
                        fi

                        if curl -fsS \
                          http://127.0.0.1:8084/api/hsm/status \
                          | jq -e '.connected == true' \
                          >/dev/null
                        then
                            HSM_OK=1
                        fi

                        if [ "$APP_OK" -eq 1 ] && \
                           [ "$HSM_OK" -eq 1 ]
                        then
                            echo "Candidate application is healthy."
                            echo "Candidate HSM connection is healthy."

                            docker rm -f springboot-hsm-candidate

                            exit 0
                        fi

                        echo "Waiting for candidate application/HSM..."
                        sleep 2
                        i=$((i + 1))
                    done

                    echo "Candidate validation FAILED."

                    docker ps -a
                    docker logs --tail 150 \
                      springboot-hsm-candidate || true

                    docker rm -f springboot-hsm-candidate \
                      2>/dev/null || true

                    exit 1
                '''
            }
        }


        stage('Deploy Docker Container') {
            steps {
                sh '''
                    echo "=== CD: Deploy Validated Image ==="

                    docker rm -f springboot-jenkins-demo || true

                    docker run -d \
                      --name springboot-jenkins-demo \
                      --env-file /etc/hsm-lab/hsm.env \
                      --group-add 985 \
                      -p 8082:8081 \
                      -v /etc/hsm-lab/softhsm.cfg:/etc/hsm-lab/softhsm.cfg:ro \
                      -v /etc/softhsm/softhsm2.conf:/etc/softhsm/softhsm2.conf:ro \
                      -v /var/lib/softhsm/tokens:/var/lib/softhsm/tokens:rw \
                      springboot-jenkins-demo:${BUILD_NUMBER}
                '''
            }
        }


        stage('Final Health Check') {
            steps {
                sh '''
                    echo "=== CD: Final Application and HSM Check ==="

                    i=1

                    while [ "$i" -le 15 ]
                    do
                        echo "Final health check attempt $i..."

                        APP_OK=0
                        HSM_OK=0

                        if curl -fsS \
                          http://127.0.0.1:8082/api/status \
                          >/dev/null
                        then
                            APP_OK=1
                        fi

                        if curl -fsS \
                          http://127.0.0.1:8082/api/hsm/status \
                          | jq -e '.connected == true' \
                          >/dev/null
                        then
                            HSM_OK=1
                        fi

                        if [ "$APP_OK" -eq 1 ] && \
                           [ "$HSM_OK" -eq 1 ]
                        then
                            echo "Application is healthy."
                            echo "HSM is connected."
                            echo "CI/CD deployment completed successfully."

                            exit 0
                        fi

                        echo "Waiting for deployed application/HSM..."
                        sleep 2
                        i=$((i + 1))
                    done

                    echo "Final deployment health check FAILED."

                    docker ps -a
                    docker logs --tail 150 \
                      springboot-jenkins-demo || true

                    exit 1
                '''
            }
        }
    }


    post {

        success {
            echo 'CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'CI/CD pipeline failed. Check the failed stage and logs.'
        }

        always {
            sh '''
                docker rm -f springboot-hsm-candidate \
                  2>/dev/null || true
            '''
        }
    }
}



