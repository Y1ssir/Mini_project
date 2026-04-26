// Sidebar.js — Premium dark sidebar with SVG icons
import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../AuthContext';

// Inline SVG icons for crisp rendering
const icons = {
  incidents: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="7" height="7" rx="1" /><rect x="14" y="3" width="7" height="7" rx="1" /><rect x="3" y="14" width="7" height="7" rx="1" /><rect x="14" y="14" width="7" height="7" rx="1" />
    </svg>
  ),
  create: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" /><line x1="12" y1="8" x2="12" y2="16" /><line x1="8" y1="12" x2="16" y2="12" />
    </svg>
  ),
  chat: (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>
  ),
};

export default function Sidebar() {
  const { user } = useAuth();

  // Get initials for avatar
  const initials = user?.name
    ? user.name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : '??';

  return (
    <div className="sidebar">
      <div className="logo">Incident Portal</div>

      <div className="nav-section">Navigation</div>

      <NavLink to="/" end className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
        <span className="nav-icon">{icons.incidents}</span>
        My Incidents
      </NavLink>
      <NavLink to="/create" className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
        <span className="nav-icon">{icons.create}</span>
        New Incident
      </NavLink>
      <NavLink to="/chat" className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
        <span className="nav-icon">{icons.chat}</span>
        Chat Support
      </NavLink>

      {/* User info footer */}
      <div className="sidebar-footer">
        <div className="sidebar-avatar">{initials}</div>
        <div>
          <div className="sidebar-user-name">{user?.name || 'User'}</div>
          <div className="sidebar-user-email">{user?.email || 'user@email.com'}</div>
        </div>
      </div>
    </div>
  );
}
