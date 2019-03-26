pipeline{
		agent {
			label 'master'
		}
		environment {
			mvnHome=tool name: 'mvn3', type: 'maven'
			mvnCmd="${mvnHome}/bin/mvn"
		}
		stages{	 
			stage('Automation mvn build'){
			  steps{
				sh label: '', script: "${mvnCmd} -e clean install dependency:copy-dependencies -Dmaven.test.skip=true -Dv=${env.BUILD_NUMBER}" 
				sh label: '', script: 'cp target/*.jar docker/'
				}									
		}
		stage('Automation docker image build and tag'){
			 steps{	
				 sh label: '', script: 'docker build -t mra-automation-${GIT_BRANCH#*/}-service docker/'
				 sh label: '', script: 'docker tag mra-automation-${GIT_BRANCH#*/}-service isl-dsdc.ca.com:5000/ms-automation-service/mra-${GIT_BRANCH#*/}-automation-service:latest'
				 
			 }
		}
		stage('Automation docker image push to repo'){
			 steps{
				 withCredentials([string(credentialsId: 'ca-password', variable: 'caDocker')]) {
				   sh label: '', script: "docker login http://isl-dsdc.ca.com:5000 -u kumaj08 -p ${caDocker}"
				}
				 sh label: '', script: 'docker push isl-dsdc.ca.com:5000/ms-automation-service/mra-${GIT_BRANCH#*/}-automation-service:latest'
			 } 
		 }	
	}								 
   post{
		always {
            echo 'Deleting the workspace'
            deleteDir()
		} 
		  
	}
 }
