package twg2.dependency.jar;

import java.util.ArrayList;
import java.util.List;

import twg2.dependency.models.LibraryJson;
import twg2.dependency.models.PackageJson;
import lombok.Getter;

public class DependencyAndDependents {
	private @Getter PackageJson packageInfo;
	private @Getter LibraryJson libraryInfo;
	private List<PackageJson> dependents;


	public DependencyAndDependents(PackageJson packageInfo, LibraryJson libraryInfo) {
		this.packageInfo = packageInfo;
		this.libraryInfo = libraryInfo;
		this.dependents = null;
	}


	public boolean isPackage() {
		return this.packageInfo != null;
	}


	public boolean isLibrary() {
		return this.libraryInfo != null;
	}


	public List<PackageJson> getDependents() {
		if(this.dependents == null) {
			this.dependents = new ArrayList<>(8);
		}
		return this.dependents;
	}


	public DependencyAndDependents addDependent(PackageJson pkg) {
		this.getDependents().add(pkg);
		return this;
	}


	@Override
	public String toString() {
		return this.packageInfo != null ? this.packageInfo.toString() : (this.libraryInfo != null ? this.libraryInfo.toString() : "no package or library info") +
				" with dependents: " + (this.dependents != null ? this.dependents.toString() : "[]");
	}

}
