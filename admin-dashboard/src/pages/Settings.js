import React from 'react';

export default function Settings() {
  return (
    <div>
      <div className="page-header">
        <h1>System Settings</h1>
        <button className="btn primary">Save Changes</button>
      </div>

      <div className="card" style={{ maxWidth: 800 }}>
        <h3 style={{ marginBottom: 20 }}>SLA Configuration</h3>
        
        <div className="form-grid">
          <div className="form-row">
            <div className="field">
              <label>High Priority SLA (Hours)</label>
              <input type="number" defaultValue={4} />
            </div>
            <div className="field">
              <label>Medium Priority SLA (Hours)</label>
              <input type="number" defaultValue={24} />
            </div>
          </div>
          
          <div className="form-row">
            <div className="field">
              <label>Low Priority SLA (Hours)</label>
              <input type="number" defaultValue={72} />
            </div>
            <div className="field">
              <label>Auto-close Resolved After (Days)</label>
              <input type="number" defaultValue={7} />
            </div>
          </div>
        </div>

        <h3 style={{ margin: '32px 0 20px' }}>Notifications</h3>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" defaultChecked />
            <span style={{ fontSize: 13 }}>Email admins on new high priority incidents</span>
          </label>
          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" defaultChecked />
            <span style={{ fontSize: 13 }}>Email users when status changes</span>
          </label>
        </div>
      </div>
    </div>
  );
}
