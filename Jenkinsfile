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
	}								 
   post{
		always {
            echo 'Deleting the workspace'
            deleteDir()
		} 
		  
	}
 }
