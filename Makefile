.PHONY: setup

setup:
	./app/gradlew -p app wrapper --gradle-version 9.2.1
	./app/gradlew -p app build