mvn install:install-file  -Dfile=target/myrest-0.1.0.jar \
                          -DgroupId=org.myrest \
                          -DartifactId=myrest \
                          -Dversion=0.1.0 \
                          -Dpackaging=jar \
                          -DlocalRepositoryPath=releases
