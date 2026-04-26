import React from 'react';
import { MOCK_INCIDENTS } from '../mockData';
import { StatusBadge, PriorityBadge } from '../components/Badge';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const navigate = useNavigate();
  const total = MOCK_INCIDENTS.length;
  const newCount = MOCK_INCIDENTS.filter(i => i.status === 'new').length;
  const inProgress = MOCK_INCIDENTS.filter(i => i.status === 'progress').length;
  const resolved = MOCK_INCIDENTS.filter(i => i.status === 'resolved').length;

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard Overview</h1>
      </div>

      <div className="stats">
        <div className="stat card">
          <div className="num">{total}</div>
          <div className="lbl">Total Incidents</div>
        </div>
        <div className="stat card">
          <div className="num" style={{ color: '#22D3EE', WebkitTextFillColor: 'initial', background: 'none' }}>{newCount}</div>
          <div className="lbl">New</div>
        </div>
        <div className="stat card">
          <div className="num" style={{ color: '#FBBF24', WebkitTextFillColor: 'initial', background: 'none' }}>{inProgress}</div>
          <div className="lbl">In Progress</div>
        </div>
        <div className="stat card">
          <div className="num" style={{ color: '#34D399', WebkitTextFillColor: 'initial', background: 'none' }}>{resolved}</div>
          <div className="lbl">Resolved</div>
        </div>
      </div>

      <h3 style={{ marginBottom: 16 }}>Recent Incidents</h3>
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
            {MOCK_INCIDENTS.slice(0, 5).map(inc => (
              <tr key={inc.id}>
                <td><span className="tag-id">{inc.id}</span></td>
                <td>{inc.title}</td>
                <td><PriorityBadge priority={inc.priority} /></td>
                <td><StatusBadge status={inc.status} /></td>
                <td>{inc.createdAt}</td>
                <td>
                  <button className="btn sm" onClick={() => navigate(`/incident/${inc.id}`)}>Manage</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
