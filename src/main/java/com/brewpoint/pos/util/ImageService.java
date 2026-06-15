package com.brewpoint.pos.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ImageService {
    private static final long MAX_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final String IMAGE_DIR = "data/product-images";

    private final Map<String, ImageIcon> cache = new HashMap<String, ImageIcon>();

    public String copyProductImage(File selectedFile) {
        if (selectedFile == null) {
            return null;
        }
        if (!selectedFile.isFile()) {
            throw new ValidationException("Không thể đọc ảnh đã chọn.");
        }
        if (selectedFile.length() > MAX_SIZE_BYTES) {
            throw new ValidationException("Ảnh tối đa 5 MB.");
        }
        String lowerName = selectedFile.getName().toLowerCase(Locale.ROOT);
        String extension;
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            extension = ".jpg";
        } else if (lowerName.endsWith(".png")) {
            extension = ".png";
        } else {
            throw new ValidationException("Chỉ chấp nhận ảnh JPG hoặc PNG.");
        }
        try {
            BufferedImage image = ImageIO.read(selectedFile);
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                throw new ValidationException("Không thể đọc ảnh đã chọn. Hãy chọn file JPG hoặc PNG khác.");
            }
            Path targetDir = Paths.get(IMAGE_DIR);
            Files.createDirectories(targetDir);
            String safeName = UUID.randomUUID().toString().replace("-", "") + extension;
            Path target = targetDir.resolve(safeName);
            Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return IMAGE_DIR + "/" + safeName;
        } catch (IOException ex) {
            throw new ValidationException("Không thể lưu ảnh sản phẩm.");
        }
    }

    public ImageIcon loadThumbnail(String relativePath, int width, int height) {
        return loadThumbnailFitted(relativePath, width, height);
    }

    public ImageIcon loadThumbnailFitted(String relativePath, int maxWidth, int maxHeight) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return placeholder(maxWidth, maxHeight);
        }
        Path path = Paths.get(relativePath);
        String key = relativePath + "|fit|" + maxWidth + "|" + maxHeight + "|" + lastModified(path);
        ImageIcon cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        try {
            if (!Files.exists(path)) {
                return placeholder(maxWidth, maxHeight);
            }
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                return placeholder(maxWidth, maxHeight);
            }
            ImageIcon icon = new ImageIcon(scaleToFit(image, maxWidth, maxHeight));
            cache.put(key, icon);
            return icon;
        } catch (IOException ex) {
            return placeholder(maxWidth, maxHeight);
        }
    }

    private BufferedImage scaleToFit(BufferedImage source, int maxWidth, int maxHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        double widthRatio = (double) maxWidth / (double) sourceWidth;
        double heightRatio = (double) maxHeight / (double) sourceHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        int targetWidth = Math.max(1, (int) Math.round(sourceWidth * ratio));
        int targetHeight = Math.max(1, (int) Math.round(sourceHeight * ratio));

        BufferedImage canvas = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(UIConstants.PLACEHOLDER_BG);
        graphics.fillRect(0, 0, maxWidth, maxHeight);
        int x = (maxWidth - targetWidth) / 2;
        int y = (maxHeight - targetHeight) / 2;
        graphics.drawImage(source.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), x, y, null);
        graphics.dispose();
        return canvas;
    }

    public ImageIcon placeholder(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(UIConstants.PLACEHOLDER_BG);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(UIConstants.TEXT_MUTED);
        graphics.setFont(UIConstants.fontBold(14f));
        String text = "Chưa có ảnh";
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, Math.max(8, (width - textWidth) / 2), height / 2);
        graphics.dispose();
        return new ImageIcon(image);
    }

    private long lastModified(Path path) {
        try {
            return Files.exists(path) ? Files.getLastModifiedTime(path).toMillis() : 0L;
        } catch (IOException ex) {
            return 0L;
        }
    }
}
