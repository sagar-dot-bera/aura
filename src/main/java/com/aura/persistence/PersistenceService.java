package com.aura.persistence;

import com.aura.inventory.Product;
import com.aura.registry.CentralRegistry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersistenceService {
    private final Path dataDir = Paths.get("data");
    private final Path inventoryFile = dataDir.resolve("inventory.json");
    private final Path transactionsFile = dataDir.resolve("transactions.json");
    private final Path configFile = dataDir.resolve("config.json");

    public PersistenceService() {
        ensureDataDir();
    }

    public void saveInventory(List<Product> products) {
        ensureDataDir();
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            sb.append("  {\"productId\":\"").append(p.getProductId()).append("\",")
                    .append("\"name\":\"").append(p.getName()).append("\",")
                    .append("\"basePrice\":").append(p.getBasePrice()).append(",")
                    .append("\"stockCount\":").append(p.getStockCount()).append(",")
                    .append("\"available\":").append(p.isAvailable()).append(",")
                    .append("\"requiredHardwareModule\":\"")
                    .append(p.getRequiredHardwareModule()).append("\"}");
            if (i < products.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        writeFile(inventoryFile, sb.toString());
    }

    public List<Product> loadInventory() {
        ensureDataDir();
        if (!Files.exists(inventoryFile)) {
            saveInventory(new ArrayList<Product>());
            return new ArrayList<Product>();
        }
        List<Product> products = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(inventoryFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("{") && line.endsWith("}") || line.endsWith("},")) {
                    String productId = extractValue(line, "productId");
                    String name = extractValue(line, "name");
                    String basePrice = extractNumber(line, "basePrice");
                    String stockCount = extractNumber(line, "stockCount");
                    String requiredModule = extractValue(line, "requiredHardwareModule");
                    if (productId != null && name != null) {
                        Product p = new Product(productId, name, Double.parseDouble(basePrice),
                                Integer.parseInt(stockCount), requiredModule);
                        products.add(p);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load inventory", e);
        }
        return products;
    }

    public void saveTransaction(String logEntry) {
        ensureDataDir();
        List<String> entries = loadTransactions();
        entries.add(logEntry);
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < entries.size(); i++) {
            sb.append("  \"").append(entries.get(i).replace("\"", "\\\"")).append("\"");
            if (i < entries.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        writeFile(transactionsFile, sb.toString());
    }

    public List<String> loadTransactions() {
        ensureDataDir();
        List<String> entries = new ArrayList<>();
        if (!Files.exists(transactionsFile)) {
            writeFile(transactionsFile, "[]\n");
            return entries;
        }
        try {
            List<String> lines = Files.readAllLines(transactionsFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"") && line.endsWith("\"")) {
                    entries.add(line.substring(1, line.length() - 1).replace("\\\"", "\""));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transactions", e);
        }
        return entries;
    }

    public void saveConfig(CentralRegistry registry) {
        ensureDataDir();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"systemStatus\":\"").append(registry.getSystemStatus()).append("\",\n");
        sb.append("  \"emergencyMode\":").append(registry.isEmergencyMode()).append(",\n");
        sb.append("  \"emergencyPurchaseLimit\":").append(registry.getEmergencyPurchaseLimit()).append(",\n");
        sb.append("  \"essentialProducts\":").append(registry.getEssentialProducts().toString()).append(",\n");
        sb.append("  \"config\":{");
        int index = 0;
        for (Map.Entry<String, Object> entry : registry.getAllConfig().entrySet()) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey()).append("\":\"")
                    .append(String.valueOf(entry.getValue())).append("\"");
            index++;
        }
        sb.append("}\n");
        sb.append("}\n");
        writeFile(configFile, sb.toString());
    }

    public void loadConfig(CentralRegistry registry) {
        ensureDataDir();
        if (!Files.exists(configFile)) {
            saveConfig(registry);
            return;
        }
        try {
            List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"systemStatus\"")) {
                    registry.setSystemStatus(extractValue(line, "systemStatus"));
                } else if (line.startsWith("\"emergencyMode\"")) {
                    String val = extractNumber(line, "emergencyMode");
                    if ("true".equalsIgnoreCase(val)) {
                        registry.activateEmergencyMode();
                    } else {
                        registry.deactivateEmergencyMode();
                    }
                } else if (line.startsWith("\"emergencyPurchaseLimit\"")) {
                    String val = extractNumber(line, "emergencyPurchaseLimit");
                    registry.setEmergencyPurchaseLimit(Integer.parseInt(val));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    private void ensureDataDir() {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory", e);
        }
    }

    private void writeFile(Path path, String content) {
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    private String extractValue(String line, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = line.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        int end = line.indexOf("\"", start);
        if (end < 0) {
            return null;
        }
        return line.substring(start, end);
    }

    private String extractNumber(String line, String key) {
        String pattern = "\"" + key + "\":";
        int start = line.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        int end = line.indexOf(",", start);
        if (end < 0) {
            end = line.indexOf("}", start);
        }
        if (end < 0) {
            end = line.length();
        }
        return line.substring(start, end).replace("\"", "").trim();
    }
}
