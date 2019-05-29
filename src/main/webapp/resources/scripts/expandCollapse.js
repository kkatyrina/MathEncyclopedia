function expandCollapse(block) {
    if (block.parentNode.classList.contains("expanded")) {
        block.parentNode.classList.remove("expanded")
    } else {
        block.parentNode.classList.add("expanded")
    }
}