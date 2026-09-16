pipeline {

    agent any
	
	options {
    disableConcurrentBuilds()
	}
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
                    echo "Application is healthy."
                    exit 0
                fi

                echo "Waiting for application..."
                sleep 2
                i=$((i + 1))
            done

            echo "Application health check failed."

            docker ps -a
            docker logs --tail 100 springboot-jenkins-demo

            exit 1
        '''
         }
	}
			
            
    }

}
