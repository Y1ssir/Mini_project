import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MOCK_INCIDENTS, MOCK_USERS } from '../mockData';
import { StatusBadge, PriorityBadge } from '../components/Badge';

export default function AdminIncidentDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [incident, setIncident] = useState(() => MOCK_INCIDENTS.find(i => i.id === id));
  
  if (!incident) return <div className="empty">Incident not found</div>;

  return (
    <div>
      <div className="page-header">
        <h1><span className="tag-id" style={{ fontSize: 20 }}>{incident.id}</span> Manage Incident</h1>
        <button className="btn" onClick={() => navigate('/incidents')}>← Back</button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: 24 }}>
        
        {/* Left Col */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="card">
            <h3 style={{ marginBottom: 12 }}>{incident.title}</h3>
            <div style={{ display: 'flex', gap: 10, marginBottom: 20 }}>
              <PriorityBadge priority={incident.priority} />
              <StatusBadge status={incident.status} />
              <span style={{ fontSize: 12, color: 'var(--text-muted)', marginLeft: 'auto' }}>
                Created: {incident.createdAt}
              </span>
            </div>
            <p style={{ color: 'var(--text-secondary)' }}>
              (Description goes here... user issue details)
            </p>
          </div>
          
          <div className="card">
            <h3 style={{ marginBottom: 16 }}>Internal Comments</h3>
            <textarea placeholder="Add a private note..." style={{ marginBottom: 12 }}></textarea>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button className="btn primary">Add Note</button>
            </div>
          </div>
        </div>

        {/* Right Col */}
        <div className="card" style={{ height: 'fit-content' }}>
          <h3 style={{ marginBottom: 16 }}>Update Status</h3>
          
          <div className="form-grid">
            <div className="field">
              <label>Status</label>
              <select value={incident.status} onChange={e => setIncident({...incident, status: e.target.value})}>
                <option value="new">New</option>
                <option value="progress">In Progress</option>
                <option value="resolved">Resolved</option>
                <option value="closed">Closed</option>
              </select>
            </div>
            
            <div className="field">
              <label>Priority</label>
              <select value={incident.priority} onChange={e => setIncident({...incident, priority: e.target.value})}>
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </div>
            
            <div className="field">
              <label>Assigned To</label>
              <select value={incident.assignedTo || ''} onChange={e => setIncident({...incident, assignedTo: e.target.value})}>
                <option value="">-- Unassigned --</option>
                {MOCK_USERS.filter(u => u.role !== 'User').map(u => (
                  <option key={u.id} value={u.name}>{u.name} ({u.role})</option>
                ))}
              </select>
            </div>
            
            <button className="btn primary" style={{ marginTop: 8, justifyContent: 'center' }}>
              Save Changes
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
