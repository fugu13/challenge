
/*
    This is a Redux-Observable application providing a lightweight interface for the supplier-transactions APIs.
    Redux-Observable is Redux using RxJS Observables to make Epics turning streams of actions into streams of other
    actions.

    For display, the app uses Handlebars. That's already causing various awkward issues with focus and such, so
    were I to add to or redo things, I would change over to React or a similar toolbox with better focus handling
    on DOM rerenders.
*/


// Constants (mostly action names)

const NAVIGATE = 'NAVIGATE';
const SUPPLIERS = 'SUPPLIERS';
const TRANSACTIONS = 'TRANSACTIONS';

const FETCH_SUPPLIERS = 'FETCH_SUPPLIERS';
const SUPPLIERS_RECEIVED = 'SUPPLIERS_RECEIVED';
const CREATE_SUPPLIER = 'CREATE_SUPPLIER';
const SUPPLIER_CREATED = 'SUPPLIER_CREATED';
const ERROR_CREATING_SUPPLIER = 'ERROR_CREATING_SUPPLIER';
const SUPPLIER_FORM_CHANGED = 'SUPPLIER_FORM_CHANGED';

const FILTER_TRANSACTIONS = 'FILTER_TRANSACTIONS';
const FETCH_TRANSACTIONS = 'FETCH_TRANSACTIONS';
const TRANSACTIONS_RECEIVED = 'TRANSACTIONS_RECEIVED';
const CLEAR_TRANSACTIONS = 'CLEAR_TRANSACTIONS';

const DEFAULT_TRANSACTIONS_FETCH = 20;


// constructors for action objects

const visitSuppliers = () => ({type: NAVIGATE, tab: SUPPLIERS});
const visitTransactions = () => ({type: NAVIGATE, tab: TRANSACTIONS});

const fetchSuppliers = () => ({type: FETCH_SUPPLIERS});

const createSupplier = (name=null, address=null, contact=null) => ({
    type: CREATE_SUPPLIER,
    supplier: {
        name,
        address,
        contact
    }
});

const supplierCreated = (supplier) => ({
    type: SUPPLIER_CREATED,
    supplier
})

const errorCreatingSupplier = (message) => ({
    type: ERROR_CREATING_SUPPLIER,
    message
});

const supplierFormChanged = (name, value) => ({
    type: SUPPLIER_FORM_CHANGED,
    fields: {
        [name]: value
    }
});

const suppliersReceived = (suppliers) => ({
    type: SUPPLIERS_RECEIVED,
    suppliers
});

const filterTransactions = (filter, value) => ({
    type: FILTER_TRANSACTIONS,
    filters: {
        [filter]: value
    }
});


const fetchTransactions = (filters={},
                           starting_timestamp=null,
                           starting_id=null,
                           count=DEFAULT_TRANSACTIONS_FETCH) => ({
    type: FETCH_TRANSACTIONS,
    starting_timestamp,
    starting_id,
    count,
    filters
});

const transactionsReceived = (transactions, next_timestamp, next_id) => ({
    type: TRANSACTIONS_RECEIVED,
    transactions,
    next_timestamp,
    next_id
});

const clearTransactions = () => ({
    type: CLEAR_TRANSACTIONS
});

// Epics

const createSupplierEpic = action$ =>
    action$.ofType(CREATE_SUPPLIER)
        .switchMap(action => Rx.Observable.ajax.post(
            "./supplier",
            action.supplier,
            {
                'Content-Type': 'application/json'
            }
        )).map(
            response => supplierCreated(response.response)
        ).catch(
            error => {
                if(error.response.length > 0) {
                    return Rx.Observable.of(errorCreatingSupplier(error.response[0].message));
                } else {
                    console.log("Error?", error);
                    return Rx.Observable.of(errorCreatingSupplier("Unknown Error"));
                }
            }
        );

const supplierCreatedEpic = action$ =>
    action$.ofType(SUPPLIER_CREATED)
        .delay(1000)
        .map(() => fetchSuppliers());

const fetchSuppliersEpic = action$ =>
    action$.ofType(FETCH_SUPPLIERS)
        .switchMap(() => Rx.Observable.ajax.get("./supplier"))
        .map(response => suppliersReceived(response.response));


const structureTransaction = (filters) => ({
    id: filters.id,
    created: filters.created,
    content: filters.content,
    supplier: {
        id: filters.supplier_id,
        name: filters.supplier_name
    }
});

const paging = (action) => {
    var pagers = "?count=" + action.count;
    if(action.starting_timestamp) {
        pagers += "&starting_timestamp=" + action.starting_timestamp + "&starting_id=" + action.starting_id;
    }
    return pagers;
};

const fetchTransactionsEpic = action$ =>
    action$.ofType(FETCH_TRANSACTIONS)
        .concatMap((action) => Rx.Observable.ajax.post(
                "./transaction/query" + paging(action),
                structureTransaction(action.filters),
                {
                    'Content-Type': 'application/json'
                }
            )
        ).map(
            (response) => transactionsReceived(response.response.transactions,
                response.response.next_timestamp,
                response.response.next_id)
        );


// TODO: with this structure, rapid filter clicking can theoretically result in improper
// transaction appending. The restructure isn't too difficult, but is hard to verify without a clear test,
// and as I don't have the node command line infrastructure in place, those aren't implemented yet
const filterTransactionsEpic = (action$, store) =>
    action$.ofType(FILTER_TRANSACTIONS)
        .concatMap(() => Rx.Observable.from([
                clearTransactions(),
                // reach into the store to find out what the current filters are
                fetchTransactions(store.getState().filters)
            ])
        );


const rootEpic = ReduxObservable.combineEpics(createSupplierEpic, supplierCreatedEpic, fetchSuppliersEpic,
                                              fetchTransactionsEpic, filterTransactionsEpic);



// Reducers

const nameSort = (a, b) => a.name.localeCompare(b.name);

const newSuppliersReducer = (state = [], action) => {
    switch (action.type) {
        case SUPPLIER_CREATED:
            // Go ahead and add right away (without ID) to improve feel of UI
            state.push(action.supplier);
            state.sort(nameSort);
            return state;
        case SUPPLIERS_RECEIVED:
            action.suppliers.sort(nameSort);
            return action.suppliers;
        default:
            return state;
    }
};

const navReducer = (state = {onSuppliers: true, page: SUPPLIERS}, action) => {
    switch (action.type) {
        case NAVIGATE:
            return {
                onSuppliers: action.tab == SUPPLIERS,
                onTransactions: action.tab == TRANSACTIONS,
                page: action.tab
            };
        default:
            return state;
    }
};

const errorReducer = (state = null, action) => {
    switch (action.type) {
        case ERROR_CREATING_SUPPLIER:
            return action.message;
        case SUPPLIER_CREATED:
            // our errors are about supplier creation, so if we make one, get rid of the error
            return null;
        case SUPPLIER_FORM_CHANGED:
            // our errors are about supplier creation, so if the person starts editing (presumably to fix), clear error
            return null;
        default:
            return state;
    }
};

const formReducer = (state = {}, action) => {
    switch (action.type) {
        case SUPPLIER_FORM_CHANGED:
            return Object.assign({}, state, action.fields);
        default:
            return state;
    }
};

const transactionsReducer = (state = { transactions: [] }, action) => {
    switch (action.type) {
        case CLEAR_TRANSACTIONS:
            return { transactions: [] };
        case TRANSACTIONS_RECEIVED:
            return {
                transactions: [].concat(state.transactions, action.transactions),
                next_timestamp: action.next_timestamp,
                next_id: action.next_id
            };
        default:
            return state;
    }
};

const filtersReducer = (state = {}, action) => {
    switch (action.type) {
        case FILTER_TRANSACTIONS:
            return Object.assign({}, state, action.filters);
        default:
            return state;
    }
};

const rootReducer = Redux.combineReducers({
    nav: navReducer,
    supplierForm: formReducer,
    error: errorReducer,
    suppliers: newSuppliersReducer,
    transactions: transactionsReducer,
    filters: filtersReducer
});


// wire everything together

const epicMiddleware = ReduxObservable.createEpicMiddleware(rootEpic);

const store = Redux.createStore(rootReducer, Redux.applyMiddleware(epicMiddleware));



// UI event functions

const submitSupplierForm = (form) => {
    // turn empty strings to nulls
    store.dispatch(createSupplier(form.name.value || null, form.address.value || null, form.contact.value || null));
};

const submitFilterForm = (form) => {
    if(form.filterBy.value && form.filterTo.value) {
        store.dispatch(filterTransactions(form.filterBy.value, form.filterTo.value));
    }
};

const formChanged = (name, value) => {
    console.log(event);
    store.dispatch(supplierFormChanged(name, value));
};

const linkToSuppliers = () => {
    store.dispatch(visitSuppliers());
};

const linkToTransactions = () => {
    store.dispatch(visitTransactions());
};

const clickFilter = (td) => {
    store.dispatch(filterTransactions(td.headers, td.textContent));
};

const clickFilterDelete = (filter) => {
    store.dispatch(filterTransactions(filter, null));
};

const clickLoadMoreTransactions = () => {
    const data = store.getState();
    // base the new transactions on the current filters and "next" values
    store.dispatch(fetchTransactions(data.filters, data.transactions.next_timestamp, data.transactions.next_id));
};


// we have one partial template for each tab in the UI, suppliers and transactions

Handlebars.registerPartial(SUPPLIERS,
    Handlebars.compile(document.getElementById("suppliers-template").innerHTML));

Handlebars.registerPartial(TRANSACTIONS,
    Handlebars.compile(document.getElementById("transactions-template").innerHTML));


// take the main template and render

const rootTemplate = Handlebars.compile(document.getElementById("root-template").innerHTML);
const rootDiv = document.getElementById("everything");

const renderApp = () => {
    const data = store.getState();
    rootDiv.innerHTML = rootTemplate(data);
};

store.subscribe(renderApp);


// fire off our initial actions to populate the data

store.dispatch(fetchSuppliers());
store.dispatch(visitSuppliers());
store.dispatch(fetchTransactions());