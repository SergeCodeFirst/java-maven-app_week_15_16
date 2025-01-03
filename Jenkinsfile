def gv

pipeline {   
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage("init") {
            steps {
                script {
                    echo "loading groovy script..."
                    gv = load "script.groovy"
                }
            }
        }

        stage("build jar") {
            steps {
                script {
                    gv.buildJar()
                }
            }
        }

        stage("build image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }

        stage("deploy") {
            steps {
                script {
                    // gv.deployApp()
                    echo 'deploying docker image to linode k8s cluster...'
                    withKubeConfig([credentialsId:'lke-credentials', serverURrl: 'https://4acbc431-4c3f-4181-85c6-00e7bf162edf.us-southeast-2-gw.linodelke.net']){
                        sh 'kubectl create deployment nginx-deployment --image=nginx'
                    }
                }
            }
        }               
    }
} 
