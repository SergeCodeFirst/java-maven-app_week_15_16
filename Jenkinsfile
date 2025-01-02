def gv

pipeline {   
    agent any
    // tools {
    //     maven 'Maven'
    // }
    stages {
        stage("init") {
            steps {
                script {
                    // gv = load "script.groovy"
                    echo "initializing the app..."
                }
            }
        }

        stage("build jar") {
            steps {
                script {
                    // gv.buildJar()
                    echo "building the jar artifact..."
                }
            }
        }

        stage("build image") {
            steps {
                script {
                    // gv.buildImage()
                    echo "building docker image..."
                }
            }
        }

        stage("deploy") {
            steps {
                script {
                    echo "deploying the app..."
                    // gv.deployApp()
                }
            }
        }               
    }
} 
