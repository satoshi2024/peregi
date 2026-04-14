
private String escapeDelimiter(String value) {
    if (value == null){
        return null;
    }
    return value.replaceAll("[|丨｜]", "★");
}