pipeline {

    agent any

    stages {

        stage('Build and Test') {
            steps {
                sh 'mvn -B clean package'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
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
                    i=1

                    while [ "$i" -le 15 ]
                    do
                        if sudo /usr/bin/systemctl is-active --quiet jenkins-demo.service \
                           && curl -fsS http://127.0.0.1:8081/api/status
                        then
                            echo ""
                            echo "Application is healthy."
                            exit 0
                        fi

                        echo "Waiting for application to start..."
                        sleep 2

                        i=$((i + 1))
                    done

                    echo "Application health check failed."
                    exit 1
                '''
            }
        }

    }

}
