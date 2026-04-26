import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8080/',   // Keycloak server URL
  realm: 'your-realm',             // Realm name (from Member 2)
  clientId: 'frontend-client'      // Client ID configured in Keycloak
});

export default keycloak;
