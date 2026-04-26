import React from 'react';

export default function Analytics() {
  return (
    <div>
      <div className="page-header">
        <h1>Analytics</h1>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        <div className="card">
          <h3 style={{ marginBottom: 16 }}>Incidents by Department</h3>
          {/* CSS Bar Chart */}
          <div className="bar-chart">
            <div className="bar-col">
              <div className="bar" style={{ height: '80%' }}></div>
              <div className="bar-label">IT</div>
            </div>
            <div className="bar-col">
              <div className="bar" style={{ height: '40%' }}></div>
              <div className="bar-label">HR</div>
            </div>
            <div className="bar-col">
              <div className="bar" style={{ height: '60%' }}></div>
              <div className="bar-label">Finance</div>
            </div>
            <div className="bar-col">
              <div className="bar" style={{ height: '30%' }}></div>
              <div className="bar-label">Ops</div>
            </div>
          </div>
        </div>

        <div className="card">
          <h3 style={{ marginBottom: 16 }}>Resolution Time (Days)</h3>
          <div className="bar-chart">
            <div className="bar-col">
              <div className="bar" style={{ height: '20%', background: 'var(--ok)' }}></div>
              <div className="bar-label">&lt; 1d</div>
            </div>
            <div className="bar-col">
              <div className="bar" style={{ height: '70%', background: 'var(--warn)' }}></div>
              <div className="bar-label">1-3d</div>
            </div>
            <div className="bar-col">
              <div className="bar" style={{ height: '40%', background: 'var(--danger)' }}></div>
              <div className="bar-label">&gt; 3d</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
