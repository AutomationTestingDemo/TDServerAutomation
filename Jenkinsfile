pipeline{
		agent {
			label 'master'
		}
		environment {
			mvnHome=tool name: 'mvn3', type: 'maven'
			mvnCmd="${mvnHome}/bin/mvn"
			gitOpenshiftBranch = sh(returnStdout: true, script: 'echo pipeline_mra_${GIT_BRANCH#*/}')
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
				 sh label: '', script: 'docker isl-dsdc.ca.com:5000/ms-automation-service/mra-${GIT_BRANCH#*/}-automation-service:latest'
			 } 
		 }
		stage('Automation trigger openshift deployment'){
			steps{
				withCredentials([usernamePassword(credentialsId: 'GIT', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
				 dir('openshift'){
					git branch: gitOpenshiftBranch, credentialsId: 'GIT', url: 'https://github.gwd.broadcom.net/dockcpdev/preview-app.git' 
					sh label: '', script: 'echo "$(date)" >> deploy'
					sh label: '', script: 'git commit -a -m "$(date)"'
					sh label: '', script: 'git push https://$GIT_USERNAME:$(echo -n $GIT_PASSWORD | od -A n -t x1 | sed "s/ /%/g")@github.gwd.broadcom.net/dockcpdev/preview-app.git'
				 }
				}
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
