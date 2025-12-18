// API Base URL
const API_BASE_URL = '/api/bigquery';

// State
let selectedDatasetId = null;
let selectedTableId = null;

// DOM Elements
const datasetsList = document.getElementById('datasetsList');
const tablesList = document.getElementById('tablesList');
const loadingDatasets = document.getElementById('loadingDatasets');
const loadingTables = document.getElementById('loadingTables');
const loadingSchema = document.getElementById('loadingSchema');
const datasetsError = document.getElementById('datasetsError');
const tablesError = document.getElementById('tablesError');
const schemaError = document.getElementById('schemaError');
const refreshDatasetsBtn = document.getElementById('refreshDatasetsBtn');
const selectedDatasetSpan = document.getElementById('selectedDataset');
const selectedTableSpan = document.getElementById('selectedTable');
const noDatasetSelected = document.getElementById('noDatasetSelected');
const noTableSelected = document.getElementById('noTableSelected');
const schemaContent = document.getElementById('schemaContent');
const schemaStats = document.getElementById('schemaStats');
const schemaTableBody = document.getElementById('schemaTableBody');

// Timing utility
function logTiming(operation, startTime, additionalInfo = {}) {
    const duration = Date.now() - startTime;
    console.log(`[TIMING] ${operation}: ${duration}ms`, additionalInfo);
    return duration;
}

// Event Listeners
refreshDatasetsBtn.addEventListener('click', loadDatasets);

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    const pageLoadStart = Date.now();
    console.log('[TIMING] ========== Page loaded, initializing application ==========');
    loadDatasets();
    logTiming('Page initialization', pageLoadStart);
});

// Load Datasets
async function loadDatasets() {
    const operationStart = Date.now();
    console.log('[TIMING] ########## Starting loadDatasets operation ##########');
    
    try {
        // Step 1: UI preparation
        const uiPrepStart = Date.now();
        showLoading(loadingDatasets);
        hideError(datasetsError);
        datasetsList.innerHTML = '';
        logTiming('Step 1/4: UI preparation', uiPrepStart);
        
        // Step 2: API call
        const apiCallStart = Date.now();
        console.log('[TIMING] Fetching datasets from API...');
        const response = await fetch(`${API_BASE_URL}/datasets`);
        const apiCallTime = logTiming('Step 2/4: API call (fetch)', apiCallStart);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // Step 3: Parse JSON
        const parseStart = Date.now();
        const datasets = await response.json();
        const parseTime = logTiming('Step 3/4: JSON parsing', parseStart);
        
        hideLoading(loadingDatasets);
        
        // Step 4: Render UI
        const renderStart = Date.now();
        if (datasets.length === 0) {
            datasetsList.innerHTML = '<li class="info-message">No datasets found</li>';
        } else {
            datasets.forEach((dataset, index) => {
                const li = createDatasetItem(dataset);
                datasetsList.appendChild(li);
            });
        }
        const renderTime = logTiming('Step 4/4: UI rendering', renderStart, { count: datasets.length });
        
        const totalTime = logTiming('########## loadDatasets total', operationStart, {
            datasets: datasets.length,
            breakdown: {
                apiCall: `${apiCallTime}ms`,
                parsing: `${parseTime}ms`,
                rendering: `${renderTime}ms`
            }
        });
        console.log(`[TIMING] ########## loadDatasets completed: ${totalTime}ms ##########`);
        
    } catch (error) {
        const totalTime = Date.now() - operationStart;
        console.error(`[TIMING] Error loading datasets after ${totalTime}ms:`, error);
        hideLoading(loadingDatasets);
        showError(datasetsError, `Failed to load datasets: ${error.message}`);
    }
}

// Create Dataset Item
function createDatasetItem(dataset) {
    const createStart = Date.now();
    
    const li = document.createElement('li');
    li.className = 'list-item';
    li.dataset.datasetId = dataset.datasetId;
    
    const nameDiv = document.createElement('div');
    nameDiv.className = 'item-name';
    nameDiv.textContent = dataset.datasetId;
    
    const detailsDiv = document.createElement('div');
    detailsDiv.className = 'item-details';
    detailsDiv.textContent = `Project: ${dataset.projectId}`;
    
    li.appendChild(nameDiv);
    li.appendChild(detailsDiv);
    
    li.addEventListener('click', () => {
        selectDataset(dataset.datasetId, li);
    });
    
    const createTime = Date.now() - createStart;
    if (createTime > 5) { // Only log if it takes more than 5ms
        console.log(`[TIMING] Dataset item creation took ${createTime}ms`);
    }
    
    return li;
}

// Select Dataset
function selectDataset(datasetId, element) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## Dataset selected: ${datasetId} ##########`);
    
    // Step 1: Update UI selection
    const uiUpdateStart = Date.now();
    document.querySelectorAll('.datasets-section .list-item').forEach(item => {
        item.classList.remove('selected');
    });
    element.classList.add('selected');
    
    selectedDatasetId = datasetId;
    selectedDatasetSpan.textContent = `(${datasetId})`;
    logTiming('Step 1/2: UI selection update', uiUpdateStart);
    
    // Step 2: Load tables for selected dataset
    loadTables(datasetId);
    
    logTiming('########## Dataset selection completed', operationStart, { datasetId });
}

// Load Tables
async function loadTables(datasetId) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## Starting loadTables for dataset: ${datasetId} ##########`);
    
    try {
        // Step 1: UI preparation
        const uiPrepStart = Date.now();
        showLoading(loadingTables);
        hideError(tablesError);
        tablesList.innerHTML = '';
        tablesList.style.display = 'none';
        noDatasetSelected.style.display = 'none';
        logTiming('Step 1/4: UI preparation', uiPrepStart);
        
        // Step 2: API call
        const apiCallStart = Date.now();
        console.log(`[TIMING] Fetching tables for dataset '${datasetId}' from API...`);
        const response = await fetch(`${API_BASE_URL}/datasets/${datasetId}/tables`);
        const apiCallTime = logTiming('Step 2/4: API call (fetch)', apiCallStart);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // Step 3: Parse JSON
        const parseStart = Date.now();
        const tables = await response.json();
        const parseTime = logTiming('Step 3/4: JSON parsing', parseStart);
        
        hideLoading(loadingTables);
        tablesList.style.display = 'block';
        
        // Step 4: Render UI
        const renderStart = Date.now();
        if (tables.length === 0) {
            tablesList.innerHTML = '<li class="info-message">No tables found in this dataset</li>';
        } else {
            tables.forEach(table => {
                const li = createTableItem(table);
                tablesList.appendChild(li);
            });
        }
        const renderTime = logTiming('Step 4/4: UI rendering', renderStart, { count: tables.length });
        
        const totalTime = logTiming(`########## loadTables for '${datasetId}' total`, operationStart, {
            dataset: datasetId,
            tables: tables.length,
            breakdown: {
                apiCall: `${apiCallTime}ms`,
                parsing: `${parseTime}ms`,
                rendering: `${renderTime}ms`
            }
        });
        console.log(`[TIMING] ########## loadTables for '${datasetId}' completed: ${totalTime}ms ##########`);
        
    } catch (error) {
        const totalTime = Date.now() - operationStart;
        console.error(`[TIMING] Error loading tables for '${datasetId}' after ${totalTime}ms:`, error);
        hideLoading(loadingTables);
        showError(tablesError, `Failed to load tables: ${error.message}`);
        tablesList.style.display = 'block';
    }
}

// Create Table Item
function createTableItem(table) {
    const createStart = Date.now();
    
    const li = document.createElement('li');
    li.className = 'list-item';
    li.dataset.tableId = table.tableId;
    
    const nameDiv = document.createElement('div');
    nameDiv.className = 'item-name';
    nameDiv.textContent = table.tableId;
    
    const detailsDiv = document.createElement('div');
    detailsDiv.className = 'item-details';
    
    let details = `Type: ${table.type || 'TABLE'}`;
    if (table.numRows) {
        details += ` • Rows: ${table.numRows.toLocaleString()}`;
    }
    detailsDiv.textContent = details;
    
    li.appendChild(nameDiv);
    li.appendChild(detailsDiv);
    
    // Add click handler to load schema
    li.addEventListener('click', () => {
        selectTable(table.tableId, li);
    });
    
    const createTime = Date.now() - createStart;
    if (createTime > 5) { // Only log if it takes more than 5ms
        console.log(`[TIMING] Table item creation took ${createTime}ms`);
    }
    
    return li;
}

// Select Table
function selectTable(tableId, element) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## Table selected: ${tableId} ##########`);
    
    // Step 1: Update UI selection
    const uiUpdateStart = Date.now();
    document.querySelectorAll('.tables-section .list-item').forEach(item => {
        item.classList.remove('selected');
    });
    element.classList.add('selected');
    
    selectedTableId = tableId;
    selectedTableSpan.textContent = `(${tableId})`;
    logTiming('Step 1/2: UI selection update', uiUpdateStart);
    
    // Step 2: Load schema for selected table
    loadSchema(selectedDatasetId, tableId);
    
    logTiming('########## Table selection completed', operationStart, { tableId });
}

// Load Schema
async function loadSchema(datasetId, tableId) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## Starting loadSchema for table: ${datasetId}.${tableId} ##########`);
    
    try {
        // Step 1: UI preparation
        const uiPrepStart = Date.now();
        showLoading(loadingSchema);
        hideError(schemaError);
        schemaContent.style.display = 'none';
        noTableSelected.style.display = 'none';
        schemaTableBody.innerHTML = '';
        logTiming('Step 1/4: UI preparation', uiPrepStart);
        
        // Step 2: API call
        const apiCallStart = Date.now();
        console.log(`[TIMING] Fetching schema for table '${datasetId}.${tableId}' from API...`);
        const response = await fetch(`${API_BASE_URL}/datasets/${datasetId}/tables/${tableId}/schema`);
        const apiCallTime = logTiming('Step 2/4: API call (fetch)', apiCallStart);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // Step 3: Parse JSON
        const parseStart = Date.now();
        const fields = await response.json();
        const parseTime = logTiming('Step 3/4: JSON parsing', parseStart);
        
        hideLoading(loadingSchema);
        schemaContent.style.display = 'block';
        
        // Step 4: Render UI
        const renderStart = Date.now();
        
        // Update stats
        schemaStats.textContent = `📊 Total Fields: ${fields.length} | Table: ${datasetId}.${tableId}`;
        
        // Render fields table
        if (fields.length === 0) {
            schemaTableBody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 30px;">No fields found</td></tr>';
        } else {
            fields.forEach((field, index) => {
                const tr = document.createElement('tr');
                
                tr.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${field.name}</td>
                    <td>${field.type}</td>
                    <td>${field.mode || 'NULLABLE'}</td>
                    <td>${field.description || '-'}</td>
                `;
                
                schemaTableBody.appendChild(tr);
            });
        }
        const renderTime = logTiming('Step 4/4: UI rendering', renderStart, { count: fields.length });
        
        const totalTime = logTiming(`########## loadSchema for '${datasetId}.${tableId}' total`, operationStart, {
            dataset: datasetId,
            table: tableId,
            fields: fields.length,
            breakdown: {
                apiCall: `${apiCallTime}ms`,
                parsing: `${parseTime}ms`,
                rendering: `${renderTime}ms`
            }
        });
        console.log(`[TIMING] ########## loadSchema for '${datasetId}.${tableId}' completed: ${totalTime}ms ##########`);
        
    } catch (error) {
        const totalTime = Date.now() - operationStart;
        console.error(`[TIMING] Error loading schema for '${datasetId}.${tableId}' after ${totalTime}ms:`, error);
        hideLoading(loadingSchema);
        showError(schemaError, `Failed to load schema: ${error.message}`);
        schemaContent.style.display = 'none';
    }
}

// Helper Functions
function showLoading(element) {
    element.style.display = 'block';
}

function hideLoading(element) {
    element.style.display = 'none';
}

function showError(element, message) {
    element.textContent = message;
    element.style.display = 'block';
}

function hideError(element) {
    element.style.display = 'none';
}

