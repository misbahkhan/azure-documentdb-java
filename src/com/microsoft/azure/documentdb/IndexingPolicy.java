package com.microsoft.azure.documentdb;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;


/**
 * Represents the indexing policy configuration for a collection.
 */
public final class IndexingPolicy extends JsonSerializable {

    private static final String DEFAULT_PATH = "/*";

    private Collection<IncludedPath> includedPaths;
    private Collection<ExcludedPath> excludedPaths;

    /**
     * Constructor.
     */
    public IndexingPolicy() {
        this.setAutomatic(true);
        this.setIndexingMode(IndexingMode.Consistent);
    }

    /**
     * Constructor.
     * 
     * @param jsonString the json string that represents the indexing policy.
     */
    public IndexingPolicy(String jsonString) {
        super(jsonString);
    }

    /**
     * Constructor.
     * 
     * @param jsonObject the json object that represents the indexing policy.
     */
    public IndexingPolicy(JSONObject jsonObject) {
        super(jsonObject);
    }

    /**
     * Sets whether automatic indexing is enabled for a collection.
     * <p>
     * In automatic indexing, documents can be explicitly excluded from indexing using RequestOptions. In manual
     * indexing, documents can be explicitly included.
     * 
     * @param automatic the automatic
     */
    public void setAutomatic(boolean automatic) {
        super.set(Constants.Properties.AUTOMATIC, automatic);
    }

    /**
     * Gets whether automatic indexing is enabled for a collection.
     * <p>
     * In automatic indexing, documents can be explicitly excluded from indexing using RequestOptions. In manual
     * indexing, documents can be explicitly included.
     * 
     * @return the automatic
     */
    public Boolean getAutomatic() {
        return super.getBoolean(Constants.Properties.AUTOMATIC);
    }

    /**
     * Sets the indexing mode (consistent or lazy).
     * 
     * @param indexingMode the indexing mode.
     */
    public void setIndexingMode(IndexingMode indexingMode) {
        super.set(Constants.Properties.INDEXING_MODE, indexingMode.name());
    }

    /**
     * Gets the indexing mode (consistent or lazy).
     * 
     * @return the indexing mode.
     */
    public IndexingMode getIndexingMode() {
        IndexingMode result = IndexingMode.Lazy;
        try {
            result = IndexingMode.valueOf(WordUtils.capitalize(super.getString(Constants.Properties.INDEXING_MODE)));
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the paths that are chosen to be indexed by the user.
     * 
     * @return the included paths.
     */
    public Collection<IncludedPath> getIncludedPaths() {
        if (this.includedPaths == null) {
            this.includedPaths = super.getCollection(Constants.Properties.INCLUDED_PATHS, IncludedPath.class);

            if (this.includedPaths == null) {
                this.includedPaths = new ArrayList<IncludedPath>();
            }
        }

        return this.includedPaths;
    }

    public void setIncludedPaths(Collection<IncludedPath> includedPaths) {
        this.includedPaths = includedPaths;
    }

    /**
     * Gets the paths that are not indexed.
     * 
     * @return the excluded paths.
     */
    public Collection<ExcludedPath> getExcludedPaths() {
        if (this.excludedPaths == null) {
            this.excludedPaths = super.getCollection(Constants.Properties.EXCLUDED_PATHS, ExcludedPath.class);

            if (this.excludedPaths == null) {
                this.excludedPaths = new ArrayList<ExcludedPath>();
            }
        }

        return this.excludedPaths;
    }

    public void setExcludedPaths(Collection<ExcludedPath> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    @Override
    void onSave() {
        // If indexing mode is not 'none' and not paths are set, set them to the defaults
        if (this.getIndexingMode() != IndexingMode.None && this.getIncludedPaths().size() == 0 &&
                this.getExcludedPaths().size() == 0) {
            IncludedPath includedPath = new IncludedPath();
            includedPath.setPath(IndexingPolicy.DEFAULT_PATH);
            this.getIncludedPaths().add(includedPath);
        }

        if (this.includedPaths != null) {
            for (IncludedPath includedPath : this.includedPaths) {
                includedPath.onSave();
            }
            super.set(Constants.Properties.INCLUDED_PATHS, this.includedPaths);
        }

        if (this.excludedPaths != null) {
            for (ExcludedPath excludedPath : this.excludedPaths) {
                excludedPath.onSave();
            }
            super.set(Constants.Properties.EXCLUDED_PATHS, this.excludedPaths);
        }
    }
}
