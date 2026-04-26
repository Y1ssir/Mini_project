// App.js
// This is the root of your app — it sets up all the pages and navigation

import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './AuthContext';
import Sidebar from './components/Sidebar';
import MyIncidents from './pages/MyIncidents';
import CreateIncident from './pages/CreateIncident';
import IncidentDetail from './pages/IncidentDetail';
import ChatBot from './pages/ChatBot';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="layout">
          <Sidebar />
          <main className="main">
            <Routes>
              <Route path="/"               element={<MyIncidents />} />
              <Route path="/create"         element={<CreateIncident />} />
              <Route path="/incident/:id"   element={<IncidentDetail />} />
              <Route path="/chat"           element={<ChatBot />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
