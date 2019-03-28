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
				sh label: '', script: "${mvnCmd} clean test -DsuitFile=xmlfiles/testfactory.xml" 
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
