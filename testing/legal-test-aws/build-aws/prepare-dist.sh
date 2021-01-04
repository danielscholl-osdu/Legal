# This script prepares the dist directory for the integration tests.
# Must be run from the root of the repostiory

set -e

OUTPUT_DIR="${OUTPUT_DIR:-dist}"

INTEGRATION_TEST_OUTPUT_DIR=${INTEGRATION_TEST_OUTPUT_DIR:-$OUTPUT_DIR}/testing/integration
INTEGRATION_TEST_OUTPUT_BIN_DIR=${INTEGRATION_TEST_OUTPUT_DIR:-$INTEGRATION_TEST_OUTPUT_DIR}/bin
INTEGRATION_TEST_SOURCE_DIR=testing
INTEGRATION_TEST_SOURCE_DIR_AWS="$INTEGRATION_TEST_SOURCE_DIR"/legal-test-aws
INTEGRATION_TEST_SOURCE_DIR_CORE="$INTEGRATION_TEST_SOURCE_DIR"/legal-test-core
echo "--Source directories variables--"
echo $INTEGRATION_TEST_SOURCE_DIR_AWS
echo $INTEGRATION_TEST_SOURCE_DIR_CORE
echo "--Output directories variables--"
echo $OUTPUT_DIR
echo $INTEGRATION_TEST_OUTPUT_DIR
echo $INTEGRATION_TEST_OUTPUT_BIN_DIR

rm -rf "$INTEGRATION_TEST_OUTPUT_DIR"
mkdir -p "$INTEGRATION_TEST_OUTPUT_DIR" && mkdir -p "$INTEGRATION_TEST_OUTPUT_BIN_DIR"
echo "Building integration testing assemblies and gathering artifacts..."
mvn -ntp install -f "$INTEGRATION_TEST_SOURCE_DIR_CORE"/pom.xml
mvn -ntp install dependency:copy-dependencies -DskipTests -f "$INTEGRATION_TEST_SOURCE_DIR_AWS"/pom.xml -DincludeGroupIds=org.opengroup.osdu -Dmdep.copyPom
cp "$INTEGRATION_TEST_SOURCE_DIR_AWS"/target/dependency/* "${INTEGRATION_TEST_OUTPUT_BIN_DIR}"
(cd "${INTEGRATION_TEST_OUTPUT_BIN_DIR}" && ls *.jar | sed -e 's/\.jar$//' | xargs -I {} echo mvn -ntp install:install-file -Dfile={}.jar -DpomFile={}.pom >> install-deps.sh)
chmod +x "${INTEGRATION_TEST_OUTPUT_BIN_DIR}"/install-deps.sh
mvn -ntp clean -f "$INTEGRATION_TEST_SOURCE_DIR_AWS"/pom.xml
cp -R "$INTEGRATION_TEST_SOURCE_DIR_AWS"/* "${INTEGRATION_TEST_OUTPUT_DIR}"/

#copy testing parent pom to output
cp ./testing/pom.xml "${OUTPUT_DIR}/testing"
