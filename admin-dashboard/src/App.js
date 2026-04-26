import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AdminSidebar from './components/AdminSidebar';
import Dashboard from './pages/Dashboard';
import AllIncidents from './pages/AllIncidents';
import AdminIncidentDetail from './pages/AdminIncidentDetail';
import Users from './pages/Users';
import Analytics from './pages/Analytics';
import Settings from './pages/Settings';

export default function App() {
  return (
    <BrowserRouter>
      <div className="layout">
        <AdminSidebar />
        <main className="main">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/incidents" element={<AllIncidents />} />
            <Route path="/incident/:id" element={<AdminIncidentDetail />} />
            <Route path="/users" element={<Users />} />
            <Route path="/analytics" element={<Analytics />} />
            <Route path="/settings" element={<Settings />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}
