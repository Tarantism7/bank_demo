
/**
 * Dashboard functionality
 */

// Show More / Show Less transactions
function toggleTransactions() {

    const extraTransactions =
        document.querySelectorAll('.extra-transaction');

    const button =
        document.getElementById('toggleTransactions');

    if (!extraTransactions.length || !button) {
        return;
    }

    const isHidden =
        extraTransactions[0].classList.contains('hidden');

    extraTransactions.forEach(row => {
        row.classList.toggle('hidden');
    });

    button.textContent = isHidden
        ? 'Show Less ↑'
        : 'Show More ↓';
}


// Initialize dashboard functionality
document.addEventListener('DOMContentLoaded', function () {

    console.log('Dashboard JavaScript initialized');

    // Future dashboard functionality can be registered here.

});
