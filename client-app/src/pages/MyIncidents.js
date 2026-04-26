// MyIncidents.js
// Shows the list of incidents created by the logged-in user

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { StatusBadge, PriorityBadge } from '../components/Badge';
import { MOCK_INCIDENTS } from '../mockData';
// TODO: import api from '../api'; — use this when backend is ready

export default function MyIncidents() {
  const [incidents, setIncidents] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // --- MOCK VERSION (no backend needed) ---
    setIncidents(MOCK_INCIDENTS);

    // --- REAL VERSION (uncomment when backend is ready) ---
    // api.get('/api/incidents/my').then(res => setIncidents(res.data));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h1>My Incidents</h1>
        <button className="btn primary" onClick={() => navigate('/create')}>
          + New Incident
        </button>
      </div>

      {incidents.length === 0 ? (
        <div className="empty">No incidents yet. Click "New Incident" to create one.</div>
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Created</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {incidents.map((inc) => (
                <tr key={inc.id}>
                  <td><span className="tag-id">{inc.id}</span></td>
                  <td>{inc.title}</td>
                  <td><PriorityBadge priority={inc.priority} /></td>
                  <td><StatusBadge status={inc.status} /></td>
                  <td>{inc.createdAt}</td>
                  <td>
                    <button
                      className="btn sm"
                      onClick={() => navigate(`/incident/${inc.id}`)}
                    >
                      View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
