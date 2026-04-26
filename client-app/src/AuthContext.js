// AuthContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import keycloak from './index'; // your Keycloak instance

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  useEffect(() => {
    keycloak.init({ onLoad: 'login-required' }).then(authenticated => {
      if (authenticated) {
        setToken(keycloak.token);

        // Extract user info from Keycloak
        const profile = {
          id: keycloak.subject,
          name: keycloak.tokenParsed?.preferred_username,
          email: keycloak.tokenParsed?.email,
          role: keycloak.tokenParsed?.realm_access?.roles?.[0] || 'user',
        };
        setUser(profile);

        // Refresh token periodically
        setInterval(() => {
          keycloak.updateToken(60).then(refreshed => {
            if (refreshed) setToken(keycloak.token);
          });
        }, 60000);
      }
    });
  }, []);

  const login = () => keycloak.login();
  const logout = () => keycloak.logout();

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
