.PHONY: setup start

setup:
	./app/gradlew -p app wrapper --gradle-version 9.2.1
	./app/gradlew -p app build

start:
	./app/gradlew -p app bootRun