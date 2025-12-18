// JDBC API Base URL
const API_BASE_URL = '/api/bigquery-jdbc';

// State
let selectedDatasetId = null;

// DOM Elements
const datasetsList = document.getElementById('datasetsList');
const tablesList = document.getElementById('tablesList');
const loadingDatasets = document.getElementById('loadingDatasets');
const loadingTables = document.getElementById('loadingTables');
const datasetsError = document.getElementById('datasetsError');
const tablesError = document.getElementById('tablesError');
const refreshDatasetsBtn = document.getElementById('refreshDatasetsBtn');
const selectedDatasetSpan = document.getElementById('selectedDataset');
const noDatasetSelected = document.getElementById('noDatasetSelected');

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
    console.log('[TIMING] ========== JDBC Page loaded, initializing application ==========');
    loadDatasets();
    logTiming('JDBC Page initialization', pageLoadStart);
});

// Load Datasets
async function loadDatasets() {
    const operationStart = Date.now();
    console.log('[TIMING] ########## Starting JDBC loadDatasets operation ##########');
    
    try {
        // Step 1: UI preparation
        const uiPrepStart = Date.now();
        showLoading(loadingDatasets);
        hideError(datasetsError);
        datasetsList.innerHTML = '';
        logTiming('Step 1/4: UI preparation', uiPrepStart);
        
        // Step 2: API call (JDBC backend)
        const apiCallStart = Date.now();
        console.log('[TIMING] Fetching datasets from JDBC API...');
        const response = await fetch(`${API_BASE_URL}/datasets`);
        const apiCallTime = logTiming('Step 2/4: JDBC API call (fetch)', apiCallStart);
        
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
        
        const totalTime = logTiming('########## JDBC loadDatasets total', operationStart, {
            datasets: datasets.length,
            breakdown: {
                apiCall: `${apiCallTime}ms`,
                parsing: `${parseTime}ms`,
                rendering: `${renderTime}ms`
            }
        });
        console.log(`[TIMING] ########## JDBC loadDatasets completed: ${totalTime}ms ##########`);
        
    } catch (error) {
        const totalTime = Date.now() - operationStart;
        console.error(`[TIMING] Error loading datasets via JDBC after ${totalTime}ms:`, error);
        hideLoading(loadingDatasets);
        showError(datasetsError, `Failed to load datasets via JDBC: ${error.message}`);
    }
}

// Create Dataset Item
function createDatasetItem(dataset) {
    const createStart = Date.now();
    
    const li = document.createElement('li');
    li.className = 'list-item';
    li.dataset.datasetId = dataset.datasetId || dataset.TABLE_SCHEM;
    
    const nameDiv = document.createElement('div');
    nameDiv.className = 'item-name';
    nameDiv.textContent = dataset.datasetId || dataset.TABLE_SCHEM;
    
    const detailsDiv = document.createElement('div');
    detailsDiv.className = 'item-details';
    detailsDiv.textContent = `Via JDBC • Project: ${dataset.projectId || 'ehc-alberto-diazraya-35c897'}`;
    
    li.appendChild(nameDiv);
    li.appendChild(detailsDiv);
    
    li.addEventListener('click', () => {
        selectDataset(dataset.datasetId || dataset.TABLE_SCHEM, li);
    });
    
    const createTime = Date.now() - createStart;
    if (createTime > 5) {
        console.log(`[TIMING] Dataset item creation took ${createTime}ms`);
    }
    
    return li;
}

// Select Dataset
function selectDataset(datasetId, element) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## JDBC Dataset selected: ${datasetId} ##########`);
    
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
    
    logTiming('########## JDBC Dataset selection completed', operationStart, { datasetId });
}

// Load Tables
async function loadTables(datasetId) {
    const operationStart = Date.now();
    console.log(`[TIMING] ########## Starting JDBC loadTables for dataset: ${datasetId} ##########`);
    
    try {
        // Step 1: UI preparation
        const uiPrepStart = Date.now();
        showLoading(loadingTables);
        hideError(tablesError);
        tablesList.innerHTML = '';
        tablesList.style.display = 'none';
        noDatasetSelected.style.display = 'none';
        logTiming('Step 1/4: UI preparation', uiPrepStart);
        
        // Step 2: API call (JDBC backend)
        const apiCallStart = Date.now();
        console.log(`[TIMING] Fetching tables for dataset '${datasetId}' from JDBC API...`);
        const response = await fetch(`${API_BASE_URL}/datasets/${datasetId}/tables`);
        const apiCallTime = logTiming('Step 2/4: JDBC API call (fetch)', apiCallStart);
        
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
        
        const totalTime = logTiming(`########## JDBC loadTables for '${datasetId}' total`, operationStart, {
            dataset: datasetId,
            tables: tables.length,
            breakdown: {
                apiCall: `${apiCallTime}ms`,
                parsing: `${parseTime}ms`,
                rendering: `${renderTime}ms`
            }
        });
        console.log(`[TIMING] ########## JDBC loadTables for '${datasetId}' completed: ${totalTime}ms ##########`);
        
    } catch (error) {
        const totalTime = Date.now() - operationStart;
        console.error(`[TIMING] Error loading tables via JDBC for '${datasetId}' after ${totalTime}ms:`, error);
        hideLoading(loadingTables);
        showError(tablesError, `Failed to load tables via JDBC: ${error.message}`);
        tablesList.style.display = 'block';
    }
}

// Create Table Item
function createTableItem(table) {
    const createStart = Date.now();
    
    const li = document.createElement('li');
    li.className = 'list-item';
    
    const nameDiv = document.createElement('div');
    nameDiv.className = 'item-name';
    nameDiv.textContent = table.tableId || table.TABLE_NAME;
    
    const detailsDiv = document.createElement('div');
    detailsDiv.className = 'item-details';
    
    let details = `Type: ${table.type || table.TABLE_TYPE || 'TABLE'}`;
    if (table.numRows) {
        details += ` • Rows: ${table.numRows.toLocaleString()}`;
    }
    details += ' • Via JDBC';
    detailsDiv.textContent = details;
    
    li.appendChild(nameDiv);
    li.appendChild(detailsDiv);
    
    const createTime = Date.now() - createStart;
    if (createTime > 5) {
        console.log(`[TIMING] Table item creation took ${createTime}ms`);
    }
    
    return li;
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

