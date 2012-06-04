mvn install:install-file  -Dfile=target/myrest-0.2.0.jar \
                          -DgroupId=org.myrest \
                          -DartifactId=myrest \
                          -Dversion=0.2.0 \
                          -Dpackaging=jar \
                          -DlocalRepositoryPath=releases
