import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MOCK_INCIDENTS } from '../mockData';
import { StatusBadge, PriorityBadge } from '../components/Badge';

export default function AllIncidents() {
  const navigate = useNavigate();
  const [filter, setFilter] = useState('all');

  const filtered = MOCK_INCIDENTS.filter(i => filter === 'all' ? true : i.status === filter);

  return (
    <div>
      <div className="page-header">
        <h1>All Incidents</h1>
        <div style={{ display: 'flex', gap: 10 }}>
          <select value={filter} onChange={e => setFilter(e.target.value)} style={{ width: 150 }}>
            <option value="all">All Status</option>
            <option value="new">New</option>
            <option value="progress">In Progress</option>
            <option value="resolved">Resolved</option>
            <option value="closed">Closed</option>
          </select>
        </div>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Dept</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Assigned To</th>
              <th>Created</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(inc => (
              <tr key={inc.id}>
                <td><span className="tag-id">{inc.id}</span></td>
                <td>{inc.title}</td>
                <td>{inc.department}</td>
                <td><PriorityBadge priority={inc.priority} /></td>
                <td><StatusBadge status={inc.status} /></td>
                <td>{inc.assignedTo || <span style={{ color: 'var(--text-muted)' }}>Unassigned</span>}</td>
                <td>{inc.createdAt}</td>
                <td>
                  <button className="btn sm" onClick={() => navigate(`/incident/${inc.id}`)}>Manage</button>
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr>
                <td colSpan="8" className="empty">No incidents found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
