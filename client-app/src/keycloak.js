import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8180/',   // Keycloak server URL
  realm: 'incident-realm',             // Realm name (from Member 2)
  clientId: 'incident-frontend'      // Client ID configured in Keycloak
});

export default keycloak;
